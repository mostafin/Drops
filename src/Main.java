import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main extends JFrame {


    private JPanel panelButtonow = new JPanel();
    private PanelAnimacji panelAnimacji = new PanelAnimacji();
    Thread watek;
    ThreadGroup threadGroup = new ThreadGroup("Grupa Kropelek");

    public Main() throws HeadlessException {
        this.setTitle("Rysowanie");
        this.setBounds(300,300,300,300);

       JButton start = (JButton)panelButtonow.add(new JButton("Start"));
       JButton stop = (JButton)panelButtonow.add(new JButton("Stop"));
       JButton dodaj = (JButton)panelButtonow.add(new JButton("Dodaj"));
       dodaj.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               dodajAnimation();
           }
       });

       stop.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               stopAnimation();
           }
       });

       start.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               startAnimation();
           }
       });
        panelAnimacji.setBackground(Color.GRAY);
        this.getContentPane().add(panelAnimacji);
        this.getContentPane().add(panelButtonow,BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void startAnimation(){
        panelAnimacji.startAnimation();
    }

    public void stopAnimation(){
       panelAnimacji.stopAnimation();
    }

    public void dodajAnimation(){
        panelAnimacji.addKropelka();
    }
    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    class PanelAnimacji extends JPanel{

        private volatile boolean zatrzymany = false;
        private Object lock = new Object();

        public void addKropelka(){

            kropelkaArrayList.add(new Kropelka());
            watek = new Thread(threadGroup,new KropelkaRunnable(kropelkaArrayList.get(kropelkaArrayList.size()-1)));
            watek.start();


            threadGroup.list();
        }

        public void startAnimation(){
            if(zatrzymany){
                zatrzymany = false;
                synchronized (lock){
                    lock.notifyAll();
                }
            }
        }

        public void stopAnimation(){
            zatrzymany  = true;
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            for(int i = 0 ; i < kropelkaArrayList.size();i++){
                g.drawImage(Kropelka.getKropelka(),kropelkaArrayList.get(i).X,kropelkaArrayList.get(i).Y,null);
            }
        }
        ArrayList<Kropelka> kropelkaArrayList = new ArrayList<Kropelka>();


        public class KropelkaRunnable implements Runnable
        {
            Kropelka kropelka;
            public KropelkaRunnable(Kropelka kropelka){
                this.kropelka = kropelka;
            }

            @Override
            public void run() {

                while (true)
                {
                    synchronized (lock){
                        while(zatrzymany){
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    this.kropelka.ruszKropelka(panelAnimacji);
                    repaint();

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

class Kropelka {
    public static Image kropelka = new ImageIcon("kropelka.gif").getImage();

    public int X = 0;
    public int Y = 0;
    public int dx = 1;
    public int dy = 1;
    public int xKropelki = kropelka.getWidth(null);
    public int yKropelki = kropelka.getHeight(null);

    public static Image getKropelka() {
        return Kropelka.kropelka;
    }

    public void ruszKropelka(JPanel panel){
        X+=dx;
        Y+=dy;

        Rectangle granicePanelu = panel.getBounds();

        if(Y + yKropelki >= granicePanelu.getMaxY()){
            Y = (int)granicePanelu.getMaxY() - yKropelki;
            dy = - dy;
        }
        if(X + xKropelki >= granicePanelu.getMaxX()){
            X = (int)granicePanelu.getMaxX() - xKropelki;
            dx = -dx;
        }
        if(Y <= granicePanelu.getMinY()){
            Y = (int) granicePanelu.getMinY();
            dy = -dy;
        }
        if(X <= granicePanelu.getMinX()){
            X = (int)granicePanelu.getMinX();
            dx = -dx;
        }
    }
}
