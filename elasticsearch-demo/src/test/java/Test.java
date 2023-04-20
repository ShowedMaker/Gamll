/**
 * @ClassName Test
 * @Description
 * @Author yzchao
 * @Date 2023/1/27 18:59
 * @Version V1.0
 */
public class Test {
    public static void main(String[] args) {

        new Thread(() -> {

            while (true){
                System.out.println("无线死循环~~");
            }

        },"yzcThread").start();

    }
}
