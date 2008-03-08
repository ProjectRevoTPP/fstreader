import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.tree.*;

public class EntryCopier extends JFrame implements ListSelectionListener, AdjustmentListener, ActionListener{
	private FileEntry entry;
	private Vector<FileEntry> batchListA;
	private Vector<FileEntry> batchListB;
	private JPanel panel = new JPanel(new GridLayout(3,2));
	private JScrollPane listAPane;
	private JScrollPane listBPane;
	private JList listAJList;
	private JList listBJList;
	private JLabel labelA = new JLabel();
	private JLabel labelB = new JLabel();
	private JButton okButton = new JButton("Replace All");
	private JButton cancelButton = new JButton("Cancel");

    public EntryCopier() {
    	super("Batch Copy");
    	listAJList = new JList();
    	listAJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	listAJList.addListSelectionListener(this);
    	listBJList = new JList();
    	listBJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	listBJList.addListSelectionListener(this);
    	listAPane = new JScrollPane(listAJList);
    	listAPane.getVerticalScrollBar().addAdjustmentListener(this);
    	listBPane = new JScrollPane(listBJList);
    	listBPane.getVerticalScrollBar().addAdjustmentListener(this);
    	listAPane.setMinimumSize(new Dimension(200,300));
    	listBPane.setMinimumSize(new Dimension(200,300));
    	labelA.setMaximumSize(new Dimension(1000,50));
    	labelB.setMaximumSize(new Dimension(1000,50));
    	
    	okButton.addActionListener(this);
    	cancelButton.addActionListener(this);
    	
    	panel.add(listAPane);
    	panel.add(listBPane);
    	panel.add(labelA);
    	panel.add(labelB);
    	panel.add(okButton);
    	panel.add(cancelButton);
    	this.add(panel);
    	this.setDefaultCloseOperation(HIDE_ON_CLOSE);
    	this.pack();
    }
    
    public void copy(FileEntry src){
    	this.entry = src;
    }
    
    public void paste(FileEntry dest, FileEntry src, boolean offset, boolean size){
    	if(offset){
    		dest.setOffset(src.getOffset());
    	}
    	
    	if(size){
    		dest.setSize(src.getSize());
    	}
    }
    
    public void paste(FileEntry dest, boolean offset, boolean size){
    	paste(dest, this.entry, offset, size);
    }
    
    public void batchInit(Vector<FileEntry> batchListA, Vector<FileEntry> batchListB){
    	if(batchListA.size() == batchListB.size()){
    		this.batchListA = batchListA;
    		this.batchListB = batchListB;
    		listAJList.setModel(new DefaultComboBoxModel(batchListA));
    		listBJList.setModel(new DefaultComboBoxModel(batchListB));
    	}else{
    		System.out.println("List size dont match");
    	}
    	showGUI();
    }
    
    public void batchPaste(boolean offset, boolean size){
    	for(int i=0; i<batchListA.size(); i++){
    		paste(batchListA.elementAt(i), batchListB.elementAt(i), offset, size);
    	}
    }
    
    public void showGUI(){
    	this.setVisible(true);
    }
    
    public void valueChanged(ListSelectionEvent e){
		JList source = (JList)e.getSource();
		JList friend;
		JLabel dest, src;
		String destPath, srcPath;
		if (source.getSelectedIndex() != -1){
			if(source == listAJList){
				friend = listBJList;
				dest = labelB;
				src = labelA;
			}else{
				friend = listAJList;
				dest = labelA;
				src = labelB;
			}
		
			friend.setSelectedIndex(source.getSelectedIndex());
			srcPath = (new TreePath(((FileEntry)source.getSelectedValue()).getPath())).toString();
			destPath = (new TreePath(((FileEntry)friend.getSelectedValue()).getPath())).toString();
			dest.setText(destPath);
			src.setText(srcPath);
		}
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e){
		JScrollBar source = (JScrollBar)e.getSource();
		JScrollBar friend = (source.getParent() == listAPane)?listBPane.getVerticalScrollBar():listAPane.getVerticalScrollBar();
		if(friend.getValue() != source.getValue()){
			friend.setValue(source.getValue());
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == okButton){
			batchPaste(true, true);
			JOptionPane.showMessageDialog(null, "Done");
			this.setVisible(false);
		}else if(source == cancelButton){
			this.setVisible(false);
		}
	}
}