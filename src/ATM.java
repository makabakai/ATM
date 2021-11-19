import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ATM extends Application {

    Transaction recorder;
    List<String> usersInfoList = new ArrayList<>();
    static List<String> usersIdList = new ArrayList<>();
    static List<String> usersPasswordList = new ArrayList<>();
    static List<String> usersBalanceList = new ArrayList<>();
    static List<String> usersInterestList = new ArrayList<>();
    static List<String> usersCreateTimeList = new ArrayList<>();

    File usersFile = new File(".\\src\\users.txt");
    Image icon = new Image("file:.\\src\\Icon.jpg");
    Stage warningStage = new Stage();
    Stage menuStage = new Stage();

    int numOfUsers;
    int currentUser;

    @Override
    public void start(Stage logStage) throws Exception {

        Scanner input = new Scanner(usersFile);
        while (input.hasNext()) {
            usersInfoList.add(input.nextLine());
        }
        input.close();

        GridPane logPane = new GridPane();
        Label title = new Label("ATM");
        Label userId = new Label("卡号:");
        Label userPassword = new Label("密码:");
        TextField inputId = new TextField();
        PasswordField inputPassword = new PasswordField();
        Button btLog = new Button("登录");
        btLog.setId("bt1");
        Button btRegist = new Button("注册");
        btRegist.setId("bt1");

        inputId.setPromptText("请输入您的8位卡号");
        inputId.setFocusTraversable(false);
        inputLimit(inputId, 8);/*添加listener，限制用户只能输入八位数字卡号*/
        inputPassword.setPromptText("请输入您的密码");
        inputPassword.setFocusTraversable(false);
        inputLimit(inputPassword, 6);  /*添加listener，限制用户只能输入六位数字密码*/

        HBox btBox = new HBox();
        btBox.getChildren().addAll(btLog, btRegist);
        btBox.setSpacing(50);

        logPane.add(title, 2, 0);
        logPane.add(userId, 1, 1);
        logPane.add(userPassword, 1, 2);
        logPane.add(inputId, 2, 1);
        logPane.add(inputPassword, 2, 2);
        logPane.add(btBox, 2, 3);
        logPane.setHgap(10);
        logPane.setVgap(20);
        logPane.setAlignment(Pos.CENTER);

        title.setStyle("-fx-font-size:30;-fx-font-family: Century Arial");
        GridPane.setMargin(title, new Insets(0, 0, 0, 30));

        initialATM();/*读入所有用户信息*/


        /*实现按下“登录”按钮后的操作*/
        btLog.setOnAction(logEvent -> {
            if (matchUsersId(inputId.getText())) {
                if (matchUsersPassword(inputPassword.getText())) {
                    showMenuStage(logStage);
                    try {
                        recorder=new Transaction(inputId.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    inputId.clear();
                    inputPassword.clear();
                }
            }
        });

        /*实现按下“注册”按钮后的操作*/
        btRegist.setOnAction(registEvent -> {
            Label lbSetPassword = new Label("设置您的密码:");
            TextField tfSetPassword = new PasswordField();
            tfSetPassword.setPromptText("输入六位数字，默认为123456");
            tfSetPassword.setPrefWidth(180);
            tfSetPassword.setFocusTraversable(false);
            Button btContinue = new Button("继续");
            btContinue.setId("registContinue");
            inputLimit(tfSetPassword, 6);/*添加listener，限制用户只能输入六位数字密码*/

            GridPane registPane = new GridPane();
            registPane.add(lbSetPassword, 0, 0);
            registPane.add(tfSetPassword, 1, 0);
            registPane.add(btContinue, 1, 1);
            registPane.setAlignment(Pos.CENTER);
            registPane.setHgap(10);
            registPane.setVgap(20);

            Scene scene = new Scene(registPane, 400, 200);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/

            Stage registStage = new Stage();
            registStage.setScene(scene);
            registStage.setTitle("注册");
            registStage.getIcons().add(icon);
            registStage.setResizable(false);
            registStage.setAlwaysOnTop(true);
            registStage.show();

            btContinue.setOnAction((continueEvent) -> {
                Account newUser;
                Label lbSuccess;
                Label lbBalance;
                Label lb;
                GridPane successPane = new GridPane();
                successPane.setAlignment(Pos.CENTER);
                Scene successScene = new Scene(successPane, 350, 200);
                successScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
                warningStage = new Stage();
                warningStage.setScene(successScene);
                warningStage.setTitle("成功");
                warningStage.getIcons().add(icon);
                warningStage.setResizable(false);
                warningStage.setAlwaysOnTop(true);
                warningStage.setOnCloseRequest(closeEvent->registStage.close());
                if (tfSetPassword.getText().length() != 6 && tfSetPassword.getText().length() != 0) {
                    showWarning("请输入6位数字密码！");
                } else if (tfSetPassword.getText().trim().equals("")) {
                    do {
                        newUser = new Account();
                    } while (usersIdList.contains(newUser.getId()));
                    lb = new Label("注册成功,请牢记您的信息");
                    lb.setId("lb1");
                    lbSuccess = new Label("用户名:" + newUser.getId());
                    lbSuccess.setId("lb1");
                    lbBalance = new Label("密码:" + newUser.getPassword());
                    lbBalance.setId("lb1");
                    successPane.add(lb, 0, 0);
                    successPane.add(lbSuccess, 0, 1);
                    successPane.add(lbBalance, 0, 2);
                    warningStage.show();
                    saveUsersInfo(newUser);
                    createRecordFile(newUser.getId());
                } else {
                    do {
                        newUser = new Account(tfSetPassword.getText());
                    } while (usersIdList.contains(newUser.getId()));
                    lb = new Label("注册成功,请牢记您的信息");
                    lb.setId("lb1");
                    lbSuccess = new Label("用户名:" + newUser.getId());
                    lbSuccess.setId("lb1");
                    lbBalance = new Label("密码:" + newUser.getPassword());
                    lbBalance.setId("lb1");
                    successPane.add(lb, 0, 0);
                    successPane.add(lbSuccess, 0, 1);
                    successPane.add(lbBalance, 0, 2);
                    warningStage.show();
                    saveUsersInfo(newUser);
                    createRecordFile(newUser.getId());
                }
            });

        });

        Scene scene = new Scene(logPane, 400, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/

        logStage.setScene(scene);
        logStage.setTitle("ATM");
        logStage.getIcons().add(icon);
        logStage.setResizable(false);
        logStage.setAlwaysOnTop(true);
        logStage.setOnCloseRequest(closeEvent -> {
            try {
                saveATM();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (warningStage.isShowing()) {
                warningStage.close();
            }
            Platform.exit();
        });
        logStage.show();

    }

    private void createRecordFile(String id) {
        try {
            new File(".\\src\\records\\"+id+".txt").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean matchUsersId(String id) {
        if (usersIdList.contains(id)) {
            currentUser = usersIdList.indexOf(id);
            return true;
        } else if (id.equals("")) {
            showWarning("请输入用户名！");
            return false;
        } else {
            showWarning("用户不存在，请先注册！");
            return false;
        }
    }

    private boolean matchUsersPassword(String password) {
        if (usersPasswordList.get(currentUser).equals(password)) {
            return true;
        } else if (password.equals("")) {
            showWarning("请输入密码！");
            return false;
        } else {
            showWarning("密码错误！");
            return false;
        }
    }

    private void showWarning(String s) {
        GridPane warningPane = new GridPane();
        Label warningLabel = new Label(s);
        warningPane.add(warningLabel, 0, 0);
        warningPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(warningPane, 200, 100);
        warningStage = new Stage();
        warningStage.setScene(scene);
        warningStage.setTitle("警告");
        warningStage.getIcons().add(icon);
        warningStage.setResizable(false);
        warningStage.setAlwaysOnTop(true);
        warningStage.show();
    }

    private void showSuccess(String s) {
        GridPane successPane = new GridPane();
        Label lbSuccess = new Label(s);
        lbSuccess.setId("lb1");
        Label lbBalance = new Label("当前余额:¥ " + usersBalanceList.get(currentUser) + "元");
        lbBalance.setId("lb1");
        successPane.add(lbSuccess, 0, 0);
        successPane.add(lbBalance, 0, 1);
        successPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(successPane, 350, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
        warningStage = new Stage();
        warningStage.setScene(scene);
        warningStage.setTitle("成功");
        warningStage.getIcons().add(icon);
        warningStage.setResizable(false);
        warningStage.setAlwaysOnTop(true);
        warningStage.show();
    }

    private void initialATM() {
        for (String s : usersInfoList) {
            String[] s1 = s.trim().split("//");
            usersIdList.add(s1[0]);
            usersPasswordList.add(s1[1]);
            usersBalanceList.add(s1[2]);
            usersInterestList.add(s1[3]);
            usersCreateTimeList.add(s1[4]);
        }
        numOfUsers = usersInfoList.size();
    }

    private void saveUsersInfo(Account newUser) {
        usersIdList.add(newUser.getId());
        usersPasswordList.add(newUser.getPassword());
        usersBalanceList.add(String.valueOf(newUser.getBalance()));
        usersInterestList.add(String.valueOf(newUser.interestRate));
        usersCreateTimeList.add(newUser.getCreateTime());
        numOfUsers++;
    }

    private void saveATM() throws IOException {
        PrintStream outPut = new PrintStream(new FileOutputStream(usersFile));
        for (int i = 0; i < numOfUsers; ++i) {
            outPut.println(usersIdList.get(i) + "//" + usersPasswordList.get(i) + "//" + usersBalanceList.get(i) + "//" + usersInterestList.get(i) + "//" + usersCreateTimeList.get(i));
        }
        outPut.close();
    }

    private void showMenuStage(Stage closeStage) {

        Image atmImage = new Image("file:.\\src\\ATM.jpg", 800, 600, true, false);
        GridPane pane = new GridPane();

        Button btBalance = new Button("余额查询");
        btBalance.setId("bt2");
        Button btSave = new Button("存款");
        btSave.setId("bt");
        Button btWithdraw = new Button("取款");
        btWithdraw.setId("bt");
        Button btTransfer = new Button("转账");
        btTransfer.setId("bt");
        Button btQueryTransaction = new Button("查询交易");
        btQueryTransaction.setId("bt2");
        Button btReload = new Button("重新登录");
        btReload.setId("bt3");
        Button btChangePassword = new Button("修改密码");
        btChangePassword.setId("bt3");

        Label menu = new Label("欢迎使用");
        menu.setId("menuLabel");
        Label welcome = new Label("欢迎您,用户" + usersIdList.get(currentUser));
        welcome.setId("welcomeLabel");

        VBox bts = new VBox();
        bts.getChildren().addAll(menu, btBalance, btSave, btWithdraw, btTransfer, btQueryTransaction);
        bts.setAlignment(Pos.CENTER);
        bts.setSpacing(30);

        VBox welcomeBox = new VBox();
        welcomeBox.getChildren().addAll(welcome, btChangePassword, btReload);
        welcomeBox.setSpacing(10);
        VBox.setMargin(btReload, new Insets(0, 0, 0, 250));
        VBox.setMargin(btChangePassword, new Insets(0, 0, 0, 250));
        VBox.setMargin(welcome, new Insets(50, 0, 0, 0));

        GridPane.setMargin(bts, new Insets(200));
        pane.add(bts, 1, 1);
        pane.add(new ImageView(atmImage), 2, 1);
        pane.add(welcomeBox, 2, 0);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane, 1000, 950);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/

        menuStage.setScene(scene);
        menuStage.setTitle("ATM");
        menuStage.getIcons().add(icon);
        menuStage.setResizable(false);
        menuStage.show();
        closeStage.hide();

        btBalance.setOnAction(balanceEvent -> {
            Label lbBalance = new Label("您的余额为:¥ " + usersBalanceList.get(currentUser) + "元");
            lbBalance.setId("lb1");
            Label lbInterestRate = new Label("当前利率" + (Double.parseDouble(usersInterestList.get(currentUser))) * 100 + "%");
            lbInterestRate.setId("lb1");

            VBox box = new VBox();
            box.getChildren().addAll(lbBalance, lbInterestRate);
            box.setAlignment(Pos.CENTER);
            box.setSpacing(10);

            Scene balanceScene = new Scene(box, 350, 200);
            balanceScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
            Stage balanceStage = new Stage();
            balanceStage.setTitle("账户余额");
            balanceStage.setScene(balanceScene);
            balanceStage.setResizable(false);
            balanceStage.getIcons().add(icon);
            balanceStage.show();

        });

        btSave.setOnAction(saveEvent -> {
            TextField tfSave = new TextField();
            Label lbHowMany = new Label("您要存的金额");
            lbHowMany.setId("lb2");
            Label emptyWarning = new Label("请输入金额！");
            emptyWarning.setTextFill(Color.RED);
            VBox saveBox = new VBox();
            Button btConfirmSave = new Button("确认");
            btConfirmSave.setId("bt3");

            saveBox.getChildren().addAll(lbHowMany, tfSave, btConfirmSave);
            saveBox.setAlignment(Pos.CENTER);
            saveBox.setSpacing(20);

            tfSave.setMaxWidth(200);
            tfSave.setAlignment(Pos.CENTER);
            inputLimit(tfSave);

            btConfirmSave.setOnAction(confirmSaveEvent -> {
                emptyWarning.setVisible(false);
                if (tfSave.getText().equals("")) {
                    saveBox.getChildren().add(emptyWarning);
                    emptyWarning.setVisible(true);
                } else {
                    usersBalanceList.set(currentUser, String.format("%.2f", (Double.parseDouble(usersBalanceList.get(currentUser)) + Double.parseDouble(tfSave.getText()))));
                    showSuccess("存款成功！");
                    recorder.recordOperation("存款",tfSave.getText());
                    try {
                        recorder.saveOperation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Scene saveScene = new Scene(saveBox, 400, 400);
            saveScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
            Stage saveStage = new Stage();
            saveStage.getIcons().add(icon);
            saveStage.setTitle("存款");
            saveStage.setScene(saveScene);
            saveStage.setResizable(false);
            saveStage.show();

        });

        btWithdraw.setOnAction(withdrawEvent -> {
            TextField tfWithdraw = new TextField();
            Label lbHowMany = new Label("您要取的金额");
            lbHowMany.setId("lb2");
            Label emptyWarning = new Label("请输入金额！");
            emptyWarning.setTextFill(Color.RED);
            VBox withdrawBox = new VBox();
            Button btConfirmWithdraw = new Button("确认");
            btConfirmWithdraw.setId("bt3");

            withdrawBox.getChildren().addAll(lbHowMany, tfWithdraw, btConfirmWithdraw);
            withdrawBox.setAlignment(Pos.CENTER);
            withdrawBox.setSpacing(20);

            tfWithdraw.setMaxWidth(200);
            tfWithdraw.setAlignment(Pos.CENTER);
            inputLimit(tfWithdraw);

            btConfirmWithdraw.setOnAction(confirmSaveEvent -> {
                emptyWarning.setVisible(false);
                if (tfWithdraw.getText().equals("")) {
                    emptyWarning.setVisible(true);
                    withdrawBox.getChildren().add(emptyWarning);
                } else if (Double.parseDouble(usersBalanceList.get(currentUser)) < Double.parseDouble(tfWithdraw.getText())) {
                    showWarning("余额不足！");
                } else {
                    usersBalanceList.set(currentUser, String.format("%.2f", Double.parseDouble(usersBalanceList.get(currentUser)) - Double.parseDouble(tfWithdraw.getText())));
                    showSuccess("取款成功！");
                    recorder.recordOperation("取款", tfWithdraw.getText());
                    try {
                        recorder.saveOperation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Scene withdrawScene = new Scene(withdrawBox, 400, 400);
            withdrawScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
            Stage withdrawStage = new Stage();
            withdrawStage.getIcons().add(icon);
            withdrawStage.setTitle("取款");
            withdrawStage.setScene(withdrawScene);
            withdrawStage.setResizable(false);
            withdrawStage.show();
        });

        btTransfer.setOnAction(transferEvent -> {
            TextField tfHowMuch = new TextField();
            TextField tfReceiver = new TextField();
            Label lbReceiver = new Label("您要转给的用户");
            lbReceiver.setId("lb2");
            Label lbHowMany = new Label("您要转账的金额");
            lbHowMany.setId("lb2");
            Label emptyWarning1 = new Label("请输入用户卡号");
            emptyWarning1.setTextFill(Color.RED);
            Label emptyWarning2 = new Label("请输入金额！");
            emptyWarning2.setTextFill(Color.RED);
            emptyWarning1.setVisible(false);
            emptyWarning2.setVisible(false);
            VBox TransferBox = new VBox();
            Button btConfirmTransfer = new Button("确认");
            btConfirmTransfer.setId("bt3");

            TransferBox.getChildren().addAll(lbReceiver, tfReceiver, lbHowMany, tfHowMuch, btConfirmTransfer, emptyWarning1, emptyWarning2);
            TransferBox.setAlignment(Pos.CENTER);
            TransferBox.setSpacing(20);

            tfHowMuch.setMaxWidth(200);
            tfHowMuch.setAlignment(Pos.CENTER);
            tfReceiver.setMaxWidth(200);
            tfReceiver.setAlignment(Pos.CENTER);
            inputLimit(tfHowMuch);
            inputLimit(tfReceiver, 8);

            btConfirmTransfer.setOnAction(confirmSaveEvent -> {
                emptyWarning1.setVisible(false);
                emptyWarning2.setVisible(false);
                if (tfHowMuch.getText().equals("")) {
                    emptyWarning2.setVisible(true);
                } else if (tfReceiver.getText().equals("")) {
                    emptyWarning1.setVisible(true);
                } else if (!usersIdList.contains(tfReceiver.getText())) {
                    showWarning("用户不存在");
                } else if (Double.parseDouble(usersBalanceList.get(currentUser)) < Double.parseDouble(tfHowMuch.getText())) {
                    showWarning("余额不足！");
                } else {
                    int receiver = usersIdList.indexOf(tfReceiver.getText());
                    usersBalanceList.set(currentUser, String.format("%.2f", Double.parseDouble(usersBalanceList.get(currentUser)) - Double.parseDouble(tfHowMuch.getText())));
                    usersBalanceList.set(receiver, String.format("%.2f", Double.parseDouble(usersBalanceList.get(receiver)) + Double.parseDouble(tfHowMuch.getText())));
                    showSuccess("转账成功！");
                    try {
                        recorder.recordOperation("转账",tfHowMuch.getText(),tfReceiver.getText());
                        recorder.saveOperation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Scene transferScene = new Scene(TransferBox, 400, 400);
            transferScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
            Stage transferStage = new Stage();
            transferStage.getIcons().add(icon);
            transferStage.setTitle("转账");
            transferStage.setScene(transferScene);
            transferStage.setResizable(false);
            transferStage.show();
        });

        btQueryTransaction.setOnAction(queryTransactionEvent->{
            TextArea taTransaction=new TextArea();
            for(int i=0;i<recorder.getNumOfOperations();++i){
               taTransaction.appendText(recorder.getOperationTime().get(i)+"    "+recorder.getOperationType().get(i)+":"+recorder.getOperationDetails().get(i)+"\n");
            }
            taTransaction.setEditable(false);
            taTransaction.setMaxHeight(800);
            taTransaction.setFont(Font.font("微软雅黑",20));

            Scene transactionScene = new Scene(taTransaction,800,800);
            Stage transactionStage = new Stage();
            transactionStage.setResizable(false);
            transactionStage.setScene(transactionScene);
            transactionStage.setTitle("查询记录");
            transactionStage.getIcons().add(icon);
            transactionStage.show();
        });

        btReload.setOnAction(reloadEvent -> {
            closeStage.show();
            menuStage.close();
        });

        btChangePassword.setOnAction(changePasswordEvent -> {
            TextField tfNewPassword = new TextField();
            Label lbNewPassword = new Label("新密码");
            lbNewPassword.setId("lb2");
            Label emptyWarning = new Label("请输入密码！");
            emptyWarning.setTextFill(Color.RED);
            Label changeSuccess = new Label("修改成功!");
            VBox saveBox = new VBox();
            Button btConfirmSave = new Button("确认");
            btConfirmSave.setId("bt3");

            saveBox.getChildren().addAll(lbNewPassword, tfNewPassword, btConfirmSave,changeSuccess);
            saveBox.setAlignment(Pos.CENTER);
            saveBox.setSpacing(20);
            changeSuccess.setVisible(false);
            tfNewPassword.setMaxWidth(200);
            tfNewPassword.setAlignment(Pos.CENTER);
            inputLimit(tfNewPassword, 6);

            btConfirmSave.setOnAction(confirmSaveEvent -> {
                emptyWarning.setVisible(false);
                if (tfNewPassword.getText().equals("")) {
                    saveBox.getChildren().add(emptyWarning);
                    emptyWarning.setVisible(true);
                } else {
                    usersPasswordList.set(currentUser, tfNewPassword.getText());
                    changeSuccess.setVisible(true);
                }
            });
            Scene saveScene = new Scene(saveBox, 400, 400);
            saveScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("ATM.css")).toExternalForm());/*使用外部样式表*/
            Stage saveStage = new Stage();
            saveStage.getIcons().add(icon);
            saveStage.setTitle("修改密码");
            saveStage.setScene(saveScene);
            saveStage.setResizable(false);
            saveStage.show();
        });

        menuStage.setOnCloseRequest(closeEvent -> {
            try {
                saveATM();
                recorder.saveOperation();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.exit();
        });

    }

    private void inputLimit(TextField tf, int maxLimit) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLimit) {
                tf.textProperty().set(newValue.substring(0, maxLimit));/*如果输入多于限制，则忽略多余部分*/
                tf.positionCaret(maxLimit);/*将光标放至末尾*/
            }
            if (newValue.length() != 0) {
                /*外层if语句是为了避免出现String index out of range: -1异常*/
                if (!((newValue.substring(newValue.length() - 1)).matches("\\d"))) {
                    /*内层if语句使用正则表达式判断用户输入的是否为数字*/
                    tf.textProperty().set(newValue.substring(0, newValue.length() - 1));/*删去非数字字符*/
                    tf.positionCaret(newValue.length() - 1);/*将光标放至末尾*/
                }
            }
        });
    }

    private void inputLimit(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() != 0) {
                /*外层if语句是为了避免出现String index out of range: -1异常*/
                if (!((newValue.substring(newValue.length() - 1)).matches("(\\d)|(\\.)"))) {
                    /*内层if语句使用正则表达式判断用户输入的是否为数字*/
                    tf.textProperty().set(newValue.substring(0, newValue.length() - 1));/*删去非数字字符*/
                    tf.positionCaret(newValue.length() - 1);/*将光标放至末尾*/
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
