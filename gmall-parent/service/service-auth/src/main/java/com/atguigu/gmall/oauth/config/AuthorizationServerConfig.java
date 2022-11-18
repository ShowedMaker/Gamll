package com.atguigu.gmall.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.security.KeyPair;


/**
    认证服务配置类 连到oauth数据库 完成密钥文件的读取和一对钥匙的读取 为了JwtAccessTokenConverter这个生成令牌的初始化
 */
@Configuration
@EnableAuthorizationServer //标识为认证类  spring.security 提供
class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    //数据源，用于从数据库获取数据进行认证操作
    @Autowired
    private DataSource dataSource;
    //jwt令牌转换器 生成jwt的工具
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    //用户自定义实现的登录校验流程
    @Autowired
    UserDetailsService userDetailsService;

    //认证授权管理器
    @Autowired
    AuthenticationManager authenticationManager;

    //令牌持久化存储接口 令牌存储对象
    @Autowired
    TokenStore tokenStore;

    /*
    客户端信息配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //以数据源的方式,自动从数据库加载oauth的配置
        clients.jdbc(dataSource).clients(clientDetails());
    }

    /***
     * 授权服务器端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(jwtAccessTokenConverter)//生成jwt的
                .authenticationManager(authenticationManager)//认证管理器 管理jwt权限的
                .tokenStore(tokenStore)                       //令牌存储 存储jwt的
                .userDetailsService(userDetailsService);     //用户信息service 自定义校验用户密码
    }

    /***
     * 授权服务器的安全配置   oauth集成好了上下文 此类配置 我们用oauth上下文的时候那些可以那些不可以
     * @param oauthServer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients()
                .passwordEncoder(new BCryptPasswordEncoder())
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }


    /** 读取密钥的配置  把配置信息放到容器中
     @ConfigurationProperties("encrypt") 从配置文件取值 密钥的配置文件位置 密钥的别名 访问密钥文件的密码和密钥的密码   密钥的类型
     public class KeyProperties {
     private String key;
     private String salt = "deadbeef";
     private boolean failOnError = true;
     private KeyStore keyStore = new KeyStore();
     */
    @Bean("keyProp")
    public KeyProperties keyProperties(){
        return new KeyProperties();
    }

    @Resource(name = "keyProp")
    private KeyProperties keyProperties;

    //客户端配置
    /*
     public JdbcClientDetailsService(DataSource dataSource) {
        this.updateClientDetailsSql = DEFAULT_UPDATE_STATEMENT;
        this.updateClientSecretSql = "update oauth_client_details set client_secret = ? where client_id = ?";
        this.insertClientDetailsSql = "insert into oauth_client_details (client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, client_id) values (?,?,?,?,?,?,?,?,?,?,?)";
        this.selectClientDetailsSql = "select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource); //JdbcClientDetailsService 里面写死了sql语句 datasource的增删改查
                                                        //表中字段官方提供 对应数据库中oauth_client_details表
    }


    /**
     * @Description 令牌的存储策略 这里使用的是JwtTokenStore，使用JWT的令牌生成方式，其实还有以下两个比较常用的方式：
     * RedisTokenStore：将令牌存储到Redis中，此种方式相对于内存方式来说性能更好
     * JdbcTokenStore：将令牌存储到数据库中，需要新建从对应的表，有兴趣的可以尝试
     * @Date 15:43 2022/11/17
     * @Param
     * @return
     */
    @Bean
    @Autowired
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        //使用JwtTokenStore生成JWT令牌
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /**
     * JWT令牌转换器   令牌增强类，用于JWT令牌和OAuth身份进行转换
     * @param customUserAuthenticationConverter
     * TokenEnhancer接口的子类 在JWT编码的令牌值 和 Oauth身份验证信息 之间进行转化
     * 项目启动时 就准备好 jwt令牌生成器了
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(CustomUserAuthenticationConverter customUserAuthenticationConverter) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(
                keyProperties.getKeyStore().getLocation(),
                keyProperties.getKeyStore().getSecret().toCharArray())
                .getKeyPair(
                        keyProperties.getKeyStore().getAlias(),
                        keyProperties.getKeyStore().getPassword().toCharArray());
        converter.setKeyPair(keyPair);
        //配置自定义的CustomUserAuthenticationConverter
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
        return converter;
    }
}

