import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Transaction {
    File usersFile;
    List<String> operations = new ArrayList<>();
    List<String> operationType = new ArrayList<>();
    List<String> operationDetails = new ArrayList<>();
    List<String> operationTime = new ArrayList<>();
    String ID;

    int numOfOperations;

    public Transaction(String usersId) throws Exception {
        this.ID=usersId;
        usersFile = new File(".\\src\\records\\" + usersId + ".txt");

        Scanner input = new Scanner(usersFile);
        while (input.hasNext()) {
            operations.add(input.nextLine());
        }
        for (String s : operations) {
            String[] s1 = s.trim().split("//");
            operationType.add(s1[0]);
            operationDetails.add(s1[1]);
            operationTime.add(s1[2]);
        }
        numOfOperations = operations.size();
        input.close();
    }

    public int getNumOfOperations() {
        return numOfOperations;
    }

    public List<String> getOperationType() {
        return operationType;
    }

    public List<String> getOperationDetails() {
        return operationDetails;
    }

    public List<String> getOperationTime() {
        return operationTime;
    }

    public void recordOperation(String operation, String amount) {
        operationType.add(operation);
        operationDetails.add(amount+"元");
        operationTime.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        numOfOperations++;
    }

    public void recordOperation(String operation, String amount, String receiver)throws IOException {
        operationType.add(operation);
        operationDetails.add(amount+"元，到"+receiver);
        operationTime.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        File receiverFile = new File(".\\src\\records\\" + receiver + ".txt");
        PrintStream outPut = new PrintStream(new FileOutputStream(receiverFile));

            outPut.append("收到转账" + "//"+amount+"元，从" + ID + "//" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\n");
        numOfOperations++;
        outPut.close();

    }

    public void saveOperation() throws IOException {
        PrintStream outPut = new PrintStream(new FileOutputStream(usersFile));
        for (int i = 0; i < numOfOperations; ++i) {
            outPut.append(operationType.get(i) + "//" + operationDetails.get(i) + "//" + operationTime.get(i)+"\n");
        }
        outPut.close();
    }

}
