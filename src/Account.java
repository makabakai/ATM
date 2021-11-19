import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Account {
    private double balance = 100;
    final private String createTime;/*创建时间不可修改*/
    double interestRate = 0.0375;
    private String password = "123456";
    final private String id;/*用户卡号不可修改*/

    public Account() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createTime = sdf.format(new Date());

        /*随机生成8位用户卡号*/
        long tmp = 0;
        do {
            tmp = ((new Random().nextLong()));
        } while (tmp <= 0);
        id = String.valueOf(tmp).substring(0, 8);
    }

    public Account(String password) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        createTime = sdf.format(new Date());

        long tmp;
        do {
            tmp = ((new Random().nextLong()));
        } while (tmp <= 0);
        id = String.valueOf(tmp).substring(0, 8);

        this.password = password;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

}
