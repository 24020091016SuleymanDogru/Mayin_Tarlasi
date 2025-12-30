package mayin;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MayinTarlasi implements MouseListener {
	JFrame frame;
	Buton[][] board = new Buton[10][10];
	int openButton;

	public MayinTarlasi() {
		openButton = 0;
		frame = new JFrame("Mayın Tarlası");
		frame.setSize(800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(10, 10));

		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				Buton b = new Buton(row, col);
				frame.add(b);
				b.addMouseListener(this);
				board[row][col] = b;
			}
		}

		generateMine();
		updateCount();

		frame.setVisible(true);
	}

	public void generateMine() {
		int i = 0;
		while (i < 10) {
			int randRow = (int) (Math.random() * board.length);
			int randCol = (int) (Math.random() * board[0].length);

			while (board[randRow][randCol].isMine()) {
				randRow = (int) (Math.random() * board.length);
				randCol = (int) (Math.random() * board[0].length);
			}
			board[randRow][randCol].setMine(true);
			i++;
		}
	}

	public void print() {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col].isMine()) {
					board[row][col].setIcon(new ImageIcon("mine.png"));
				} else {
					board[row][col].setText(board[row][col].getCount() + "");
					board[row][col].setEnabled(false);
				}
			}
		}
	}
	
	public void printMine() {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col].isMine()) {
					board[row][col].setIcon(new ImageIcon("mine.png"));
				}
			}
		}
	}

	public void updateCount() {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				if (board[row][col].isMine()) {
					counting(row, col);
				}
			}
		}
	}

	public void counting(int row, int col) {
		for (int i = row - 1; i <= row + 1; i++) {
			for (int k = col - 1; k <= col + 1; k++) {

				if (i >= 0 && k >= 0 && i < board.length && k < board[0].length) {
					if (!board[i][k].isMine()) { 
						int value = board[i][k].getCount();
						board[i][k].setCount(value + 1);
					}
				}
			}
		}
	}
	
	public void oyunuSifirla(){
		frame.setVisible(false);
		new MayinTarlasi();
	}

	public void open(int r, int c) {
	    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || !board[r][c].isEnabled()) {
	        return;
	    } 
	    
	    else if (board[r][c].getCount() != 0) {
	        board[r][c].setText(board[r][c].getCount() + "");
	        board[r][c].setEnabled(false);
	        openButton++;
	    } 
	    
	    else {
	        openButton++;
	        board[r][c].setEnabled(false);
	        
	        open(r - 1, c);     
	        open(r + 1, c);     
	        open(r, c - 1);     
	        open(r, c + 1);    
	        open(r - 1, c - 1);
	        open(r - 1, c + 1); 
	        open(r + 1, c - 1); 
	        open(r + 1, c + 1); 
	    }
	}
	
	public void oyunBittiSorusu() {
	    int secim = JOptionPane.showConfirmDialog(frame, "Tekrar Oynamak İster Misiniz?", "Oyun Bitti", JOptionPane.YES_NO_OPTION);
	    
	    if (secim == JOptionPane.YES_OPTION) {
	        oyunuSifirla();
	    } else {
	        System.exit(0);
	    }
	}
	
	public void ekraniTitret() {
	    new Thread(new Runnable() {
	        @Override
	        public void run() {
	            try {
	                java.awt.Point orijinalKonum = frame.getLocation();
	                int siddet = 10; 
	                
	                for (int i = 0; i < 20; i++) { 
	                    int x = (int) (orijinalKonum.x + Math.random() * siddet - (siddet / 2));
	                    int y = (int) (orijinalKonum.y + Math.random() * siddet - (siddet / 2));
	                    frame.setLocation(x, y);
	                    Thread.sleep(30); 
	                }
	                
	                frame.setLocation(orijinalKonum); 
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }).start();
	}
	
	public void sesCal(String dosyaAdi) {
	    try {
	        File sesDosyasi = new File(dosyaAdi); 
	        if (sesDosyasi.exists()) {
	            Clip clip = AudioSystem.getClip();
	            clip.open(AudioSystem.getAudioInputStream(sesDosyasi));
	            clip.start();
	        } else {
	            System.out.println("Ses dosyası bulunamadı aga!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	    Buton b = (Buton) e.getComponent();

	    if (e.getButton() == 1) {
	        System.out.println("sol tık");
	        
	        if (b.isMine()) {
	            sesCal("patlama.wav"); 
	            
	            
	            ekraniTitret();
	         
	            print(); 
	            JOptionPane.showMessageDialog(frame, "Mayına Bastınız Oyun Bitti !");
	            
	            oyunBittiSorusu();
	        } 
	        else {
	            open(b.getRow(), b.getCol());
	            
	            if (openButton == (board.length * board[0].length) - 10) {
	                JOptionPane.showMessageDialog(frame, "Tebrikler Oyunu Kazandınız !");
	                print();
	                
	                oyunBittiSorusu(); 
	            }
	        }
	    } 
	    
	    else if (e.getButton() == 3) {
	        System.out.println("sağ tık");
	        if (!b.isFlag()) {
	            b.setIcon(new ImageIcon("flag.png"));
	            b.setFlag(true);
	        } else {
	            b.setIcon(null);
	            b.setFlag(false);
	        }
	    }
	    
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}