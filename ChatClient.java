import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.Scanner;


public class ChatClient implements ActionListener
{
    // initialize socket and input output streams 
    private Socket socket = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private String storeAllString = "";
    private JPanel messagePanel;
    private JTextField userInput;
    private JTextArea display;
    private JButton sendButton;
    static String id;

	// constructor to put ip address and port
    public ChatClient(String address, int port, String number)  {
    	
    	id = number;
        // render GUI
        render();

        // establish a connection 
        try
        {
            socket = new Socket(address, port);

            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            // sends output to the socket 
            out = new DataOutputStream(socket.getOutputStream());
            
            out.writeUTF(number);
            
            // readMessage thread
            Thread readMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {

                    while (true) {
                        try {
                            // read the message sent to this client
                            String msg = in.readUTF();
                            String msg2 = in.readUTF();
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    display.append(msg);
                                    display.append(msg2);
                                    display.append("\n");
                                    saveFile();
                                }
                            });
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }

                }
            });

            readMessage.start();
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public void render() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));

        messagePanel = new JPanel();
        messagePanel.setPreferredSize(new Dimension(100, 50));

        display = new JTextArea();
        panel.add(new JScrollPane(display));
        display.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) display.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        userInput = new JTextField(50);
        userInput.setSize(new Dimension(200, 50));
        messagePanel.add(userInput);

        sendButton = new JButton("Send");
        messagePanel.add(sendButton);
        sendButton.addActionListener(this);

        JFrame frame = new JFrame();
        frame.add(panel, BorderLayout.CENTER);
        frame.add(messagePanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Zap Chat Bot");
        frame.getRootPane().setDefaultButton(sendButton);
        frame.pack();
        frame.setSize(750, 500);
        frame.setVisible(true);

        readFile();

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveFile();
                if (JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                	// close the connection
                    try {
                        out.writeUTF("Over");
                    	socket.close();
                        in.close();
                        out.close();
                    } catch(IOException i)
                    {
                       System.out.println(i);
                    }
                    System.exit(0);
                }
            }
        });

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public void readFile(){
        try{
            FileReader read = new FileReader("save/ "+ id + ".txt");

            if (read != null) {
                Scanner scan = new Scanner(read);
                while(scan.hasNextLine()){
                    String temp = scan.nextLine()+"\n";
                    storeAllString = storeAllString + temp;
                }
                display.setText(storeAllString);
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void saveFile() {
        File writeFile;
        Writer writer = null;

        writeFile = new File("save/ "+ id + ".txt");
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(writeFile), "utf-8"));
            display.write(writer);
        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // keep reading until "Over" is input
        //String responseFromServer = "";
            try
            {
                out.writeUTF(userInput.getText());
//                responseFromServer = in.readUTF();
//                String botResponse = in.readUTF();
//                display.append(responseFromServer);
//                display.append(botResponse);
//                display.append("\n");
                userInput.setText("");
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
    }
}