
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javazoom.jl.player.Player;
class CustomPlayer {
    public Player player;
    private FileInputStream FIS;
    private BufferedInputStream BIS;
    private boolean canResume;
    private String path;
    private int total;
    private int stopped;
    private boolean valid;

    public CustomPlayer(){
        player = null;
        FIS = null;
        valid = false;
        BIS = null;
        path = null;
        total = 0;
        stopped = 0;
        canResume = false;
    }

    public boolean canResume(){
        return canResume;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void pause(){
        try{
            stopped = FIS.available();
            player.close();
            FIS = null;
            BIS = null;
            player = null;
            if(valid) canResume = true;
        }catch(Exception e){
            System.out.println();
        }
    }

    public void resume(){
        if(!canResume) return;
        if(play(total-stopped)) canResume = false;
    }

    public boolean play(int pos){
        valid = true;
        canResume = false;
        try{
            FIS = new FileInputStream(path);
            total = FIS.available();
            if(pos > -1) FIS.skip(pos);
            BIS = new BufferedInputStream(FIS);
            player = new Player(BIS);
            new Thread(
                    new Runnable(){
                        public void run(){
                            try{
                                player.play();
                            }catch(Exception e){
                                JOptionPane.showMessageDialog(null, "Error playing mp3 file");
                                valid = false;
                            }
                        }
                    }
            ).start();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error playing mp3 file");
            valid = false;
        }
        return valid;
    }
}

class MusicPlayer implements ActionListener {
    JFrame frame;
    JLabel songName;
    JButton select;

    JPanel playerPanel, controlPanel;
    Icon iconPlay, iconPause, iconResume, iconStop;
    JButton playBtn, pauseBtn, resumeBtn, stopBtn;
    JFileChooser fileChooser;
    File myFile = null;
    String filename, filePath;

    CustomPlayer CPlayer;

    public MusicPlayer() {

        //Calling initUI() method to initiliaze UI
        initUI();
        //Calling addActionEvents() methods to add actions
        addActionEvents();

        CPlayer = new CustomPlayer();
    }

    public void initUI() {

        //Setting songName Label to center
        songName = new JLabel("", SwingConstants.CENTER);

        //Creating button for selecting a song
        select = new JButton("Select Mp3");

        //Creating Panels
        playerPanel = new JPanel(); //Music Selection Panel
        controlPanel = new JPanel(); //Control Selection Panel

        //Creating icons for buttons
        iconPlay = new ImageIcon("F:\\intern project\\codeclause\\music player java\\MusicPlayer\\src\\music-player-icons\\play-button.png");
        iconPause = new ImageIcon("F:\\intern project\\codeclause\\music player java\\MusicPlayer\\src\\music-player-icons\\pause-button.png");
        iconResume = new ImageIcon("F:\\intern project\\codeclause\\music player java\\MusicPlayer\\src\\music-player-icons\\resume-button.png");
        iconStop = new ImageIcon("F:\\intern project\\codeclause\\music player java\\MusicPlayer\\src\\music-player-icons\\stop-button.png");

        //Creating image buttons
        playBtn = new JButton(iconPlay);
        pauseBtn = new JButton(iconPause);
        resumeBtn = new JButton(iconResume);
        stopBtn = new JButton(iconStop);

        //Setting Layout of PlayerPanel
        playerPanel.setLayout(new GridLayout(2, 1));

        //Addings components in PlayerPanel
        playerPanel.add(select);
        playerPanel.add(songName);

        //Setting Layout of ControlPanel
        controlPanel.setLayout(new GridLayout(1, 4));

        //Addings components in ControlPanel
        controlPanel.add(playBtn);
        controlPanel.add(pauseBtn);
        controlPanel.add(resumeBtn);
        controlPanel.add(stopBtn);

        //Setting buttons background color
        playBtn.setBackground(Color.WHITE);
        pauseBtn.setBackground(Color.WHITE);
        resumeBtn.setBackground(Color.WHITE);
        stopBtn.setBackground(Color.WHITE);

        //Initialing the frame
        frame = new JFrame();

        //Setting Frame's Title
        frame.setTitle("Tharun's Music Player");

        //Adding panels in Frame
        frame.add(playerPanel, BorderLayout.NORTH);
        frame.add(controlPanel, BorderLayout.SOUTH);

        //Setting Frame background color
        frame.setBackground(Color.white);
        frame.setSize(400, 200);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void addActionEvents() {
        //registering action listener to buttons
        select.addActionListener(this);
        playBtn.addActionListener(this);
        pauseBtn.addActionListener(this);
        resumeBtn.addActionListener(this);
        stopBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(select)) {
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("F:\\intern project\\codeclause\\music player web\\musics"));
            fileChooser.setDialogTitle("Select Mp3");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files", "mp3"));
            if (fileChooser.showOpenDialog(select) == JFileChooser.APPROVE_OPTION) {
                myFile = fileChooser.getSelectedFile();
                filename = fileChooser.getSelectedFile().getName();
                filePath = fileChooser.getSelectedFile().getPath();
                CPlayer.setPath(filePath);
                songName.setText("File Selected : " + filename);
            }
        }
        if (e.getSource().equals(playBtn)) {
            //starting play thread
            if (filename != null) {
                songName.setText("Now playing : " + filename);
                CPlayer.play(-1);
            } else {
                songName.setText("No File was selected!");
            }
        }
        if (e.getSource().equals(pauseBtn)) {
            //code for pause button
            if (filename != null) {
                try {
                    //player.close();
                    CPlayer.pause();

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (e.getSource().equals(resumeBtn)) {
            //starting resume thread
            if (filename != null) {
                CPlayer.resume();
            } else {
                songName.setText("No File was selected!");
            }
        }
        if (e.getSource().equals(stopBtn)) {
            //code for stop button
            if (filename != null) {
                CPlayer.pause();
                myFile = null;
                filename=null;
                songName.setText("");
            }

        }

    }



    public static void main(String[] args) {
        MusicPlayer mp = new MusicPlayer();
    }
}