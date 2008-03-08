import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.DataInputStream;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

public class FstReader extends JPanel implements TreeSelectionListener, ActionListener, ItemListener, MenuListener{
	// Class attributes
	// fst Related
	private File fstFile;
	private Vector<FileEntry> fileEntry = new Vector<FileEntry>();
	
	// Function related
	private String searchString;
	private EntryCopier entryCopier = new EntryCopier();
	
	// GUI
	// JTree
	private JScrollPane treeView;
	private JTree tree = new JTree(new String[]{"ROOT"});
	
	// JTable
	private Object[][] tableData = {{"Entry #",""},{"Path",""},{"Name",""},{"StrOffset",""},{"Offset",""},{"Size",""}};
	private String[] tableHeader = {"Key", "Value"};
	private JTable table = new JTable(tableData, tableHeader);
	private JScrollPane tableView;
	
	// JFrame
	private final String DEFAULT_TITLE = "fst Reader v0.3 beta by SleepyPrince";
	private JFrame frame = new JFrame(DEFAULT_TITLE);
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	
	// JMenuBar
	private JMenuBar menuBar = new JMenuBar();
	// File
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openFile = new JMenuItem("Open...");
	private JMenuItem saveFile = new JMenuItem("Save...");
	private JMenuItem reloadFile = new JMenuItem("Reload");
	private JMenuItem closeFile = new JMenuItem("Close");
	private JMenuItem exitJava = new JMenuItem("Exit");
	// View
	private JMenu viewMenu = new JMenu("View");
	private JMenuItem expandTree = new JMenuItem("Expand All");
	private JMenuItem collapseTree = new JMenuItem("Collapse All");
	// Find
	private JMenu findMenu = new JMenu("Edit");
	private JMenuItem findNode = new JMenuItem("Find...");
	private JMenuItem findNext = new JMenuItem("Find Next");
	private JMenuItem findPrev = new JMenuItem("Find Previous");
	private JMenuItem batchFind = new JMenuItem("Batch...");
	
	// File related
	private JFileChooser loadFile = new JFileChooser(new File("."));
	
	// Tree Popup Menu
	private JPopupMenu treePopup = new JPopupMenu();
	private JMenuItem treePopupCopy = new JMenuItem("Copy Info");
	private JMenuItem treePopupPasteOffset = new JMenuItem("Paste Offset");
	private JMenuItem treePopupPasteSize = new JMenuItem("Paste Size");
	private JMenuItem treePopupPasteBoth = new JMenuItem("Paste Both");
	private MouseListener popupListener;
	
	// Class methods
	// Constructers
	public FstReader(){
		super(new GridLayout(1,0));
		
		// MenuBar
		// File Menu
		fileMenu.setMnemonic(KeyEvent.VK_F);
			
		openFile.addActionListener(this);
		openFile.setMnemonic(KeyEvent.VK_O);
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		
		saveFile.addActionListener(this);
		saveFile.setMnemonic(KeyEvent.VK_S);
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		
		reloadFile.addActionListener(this);
		reloadFile.setMnemonic(KeyEvent.VK_R);
		reloadFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		
		closeFile.addActionListener(this);
		closeFile.setMnemonic(KeyEvent.VK_C);
		closeFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		
		exitJava.addActionListener(this);
		exitJava.setMnemonic(KeyEvent.VK_X);
		exitJava.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));

		// Find Menu
		findMenu.setMnemonic(KeyEvent.VK_E);
		findNode.addActionListener(this);
		findNode.setMnemonic(KeyEvent.VK_E);
		findNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		
		findNext.addActionListener(this);
		findNext.setMnemonic(KeyEvent.VK_N);
		findNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		
		findPrev.addActionListener(this);
		findPrev.setMnemonic(KeyEvent.VK_P);
		findPrev.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, ActionEvent.SHIFT_MASK));
		
		batchFind.addActionListener(this);
		batchFind.setMnemonic(KeyEvent.VK_B);
		batchFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		
		// View Menu
		viewMenu.setMnemonic(KeyEvent.VK_V);
			
		expandTree.addActionListener(this);
		expandTree.setMnemonic(KeyEvent.VK_E);
		
		collapseTree.addActionListener(this);
		collapseTree.setMnemonic(KeyEvent.VK_C);
		
		// Add MenuItems
		menuBar.add(fileMenu);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(reloadFile);
		fileMenu.add(closeFile);
		fileMenu.addSeparator();
		fileMenu.add(exitJava);
		
		menuBar.add(findMenu);
		findMenu.add(findNode);
		findMenu.add(findNext);
		findMenu.add(findPrev);
		findMenu.addSeparator();
		findMenu.add(batchFind);
		
		menuBar.add(viewMenu);
		viewMenu.add(expandTree);
		viewMenu.add(collapseTree);
		
		// Tree
		tree.setVisible(false);
		tree.setRootVisible(true);
		treeView = new JScrollPane(tree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    	
    	treeView = new JScrollPane(tree);
    	treeView.setMinimumSize(new Dimension(300,500));
    	splitPane.setTopComponent(treeView);
		
		// Tree Popup
		// Popup listener
    	popupListener = new PopupListener(treePopup);
		
		treePopupCopy.addActionListener(this);
		treePopupCopy.setMnemonic(KeyEvent.VK_C);
		
		treePopupPasteOffset.addActionListener(this);
		treePopupPasteOffset.setMnemonic(KeyEvent.VK_O);
		
		treePopupPasteSize.addActionListener(this);
		treePopupPasteSize.setMnemonic(KeyEvent.VK_S);
		
		treePopupPasteBoth.addActionListener(this);
		treePopupPasteBoth.setMnemonic(KeyEvent.VK_B);
		
		treePopup.add(treePopupCopy);
		treePopup.addSeparator();
		treePopup.add(treePopupPasteOffset);
		treePopup.add(treePopupPasteSize);
		treePopup.add(treePopupPasteBoth);
			
		// Table
		tableView = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(70);
		table.getColumnModel().getColumn(0).setMinWidth(70);
		table.getColumnModel().getColumn(0).setMaxWidth(70);
		table.getColumnModel().getColumn(1).setPreferredWidth(400);
		
		// SplitPane
    	splitPane.setDividerLocation(300);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(tableView);
		treeView.setMinimumSize(new Dimension(300,800));
		tableView.setMinimumSize(new Dimension(300,800));
		
		// JPanel		
		add(splitPane);
    	
    	// File Related
    	loadFile.setFileFilter(new FileNameExtensionFilter("bin file", "bin"));
		
		// JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setMinimumSize(new Dimension(800,600));
		frame.add(this);
		frame.pack();
	}
	
	// Fst File
	private void loadFile(File filename){
		try{
			// Read File
			fstFile = filename;
			InputStream is = new FileInputStream(fstFile);	// open streams
			DataInputStream ds = new DataInputStream(is);
			ds.skipBytes(8);	// Skip first 0x8
			fileEntry.setSize(ds.readInt());	// create array with size
			ds.close();	// close streams
			is.close();
			
			// Read Entry
			readEntry();
			
			// Create Tree
			refreshTree();
			
			// Clear Table
			clearTable();
		}catch(FileNotFoundException fnfe){
			//System.out.println("File not Found. "+fnfe.getMessage());
		}catch(IOException ioe){
			//System.out.println(ioe.getMessage());
		}
	}
	
	private boolean validateFile(){
		try{
			InputStream is = new FileInputStream(fstFile);	// open streams
			DataInputStream ds = new DataInputStream(is);
			
			ds.close();	// close streams
			is.close();
		}catch(IOException ioe){
			System.out.println("DEBUG:"+ioe.getMessage());
		}
		return true;
	}
	
	public void readEntry(){
		try{
			InputStream is = new FileInputStream(fstFile);	// open streams
			DataInputStream ds = new DataInputStream(is);
			int byteread;	// byte buffer
			byte[] str = new byte[255];
			int count;
			
			boolean type;	// attributes for fileEntry[]
			int strOffset;
			int offset;
			int size;
			FileEntry parent = null;
			
			// Read entry data (without name);
			for(int i=0; i<fileEntry.size(); i++){
				byteread = ds.readInt();	//
				type = ((byteread & 0x01000000) != 0);
				strOffset = (byteread & ~0x01000000);
				offset = ds.readInt();
				size = ds.readInt();
				fileEntry.setElementAt(new FileEntry(type, strOffset, offset, size),i);
				if(parent != null){
					parent.add((FileEntry)fileEntry.elementAt(i));
				}
				if(type){
					parent = (FileEntry)fileEntry.elementAt(i);
				}
				while(parent.getParent() != null && i == parent.getSize()-1){
					parent = parent.getParent();
				}
			}
			
			// ROOT
			fileEntry.elementAt(0).setName("ROOT");
			
			// Read name
			for(int i=1; i<fileEntry.capacity(); i++){
				count = 0;
				while((str[count++] = ds.readByte()) != 0){
				}
				fileEntry.elementAt(i).setName(new String(str, 0, count-1));
			}

			ds.close();	// close streams
			is.close();
		}catch(IOException ioe){
			//System.out.println(ioe.getMessage());
		}
	}
	
	public void printEntry(){
		printEntry(3);
	}
	
	public void printEntry(int filter){
		// Print fileEntry array
		for(int i=0; i<fileEntry.size(); i++){
			if( ((filter & 1) !=0 && fileEntry.elementAt(i).getType()) || ((filter & 2) !=0 && !fileEntry.elementAt(i).getType()) ){
				System.out.println(i);
				System.out.println(fileEntry.elementAt(i));
				System.out.println();
			}
		}
	}
	
	public void writeEntry(String output){
		writeEntry(new File(output));
	}
	
	public void writeEntry(File output){
		try{
			OutputStream os = new FileOutputStream(output);
			DataOutputStream dos = new DataOutputStream(os);
			byte[] str;
			
			// write file entries (without names)
			for(int i=0; i<fileEntry.size(); i++){
				// 1st 4 bytes (type + strOffset)
				if(fileEntry.elementAt(i).getType()){
					dos.writeInt(fileEntry.elementAt(i).getStrOffset() | 0x01000000);
				}else{
					dos.writeInt(fileEntry.elementAt(i).getStrOffset());
				}
				
				// offset
				dos.writeInt(fileEntry.elementAt(i).getOffset());
				
				// size
				dos.writeInt(fileEntry.elementAt(i).getSize());
			}
			
			// write file names
			for(int i=1; i<fileEntry.size(); i++){
				str = fileEntry.elementAt(i).getName().getBytes("US-ASCII");
				dos.write(str,0,str.length);
				dos.writeByte(0);
			}
			
			// ending 00
			dos.writeByte(0);
			
			// add padding
			//int padding = (12-dos.size()%12)%12;
			long padding = fstFile.length() - dos.size();
			for(long i=0; i<padding; i++){
				dos.writeByte(0);
			}
			
			dos.flush();	// flush & close streams
			dos.close();
			os.close();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
	}
	
	public int getNumOfFiles(){
		return fileEntry.size();
	}
	
	// GUI
	public void showGUI(){
//		try{
//		 	//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//			SwingUtilities.updateComponentTreeUI(this);
//		} catch( Exception e){}		
		frame.setVisible(true);
	}
	
	// JTree
	private void refreshTree(){
		tree.setModel(new DefaultTreeModel(fileEntry.elementAt(0)));
		tree.addTreeSelectionListener(this);
    	tree.addMouseListener(popupListener);
   	
     	// Update JFrame title
    	if(fstFile != null && fstFile.isFile()){
			frame.setTitle(DEFAULT_TITLE+" - "+fstFile.getName());
			tree.setVisible(true);
		}else{
			frame.setTitle(DEFAULT_TITLE);
			tree.setVisible(false);
		}
    	frame.pack();
	}
	
	private void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
    
        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }
    
    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
    
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
	
	// Jtree Search
	private TreePath locateNode(JTree tree, Vector<FileEntry> entries, String name, int start, boolean forward, boolean suppress){
		TreePath result = null;
		if(forward){
			for(int i=start; i<entries.size(); i++){
				if(entries.elementAt(i).getName().toLowerCase().indexOf(name.toLowerCase()) != -1){
					result = new TreePath(entries.elementAt(i).getPath());
					break;
				}
			}
		}else{
			for(int i=start; i>=0; i--){
				if(entries.elementAt(i).getName().toLowerCase().indexOf(name.toLowerCase()) != -1){
					result = new TreePath(entries.elementAt(i).getPath());
					break;
				}
			}
		}
		if(result == null){
			if(!suppress){
				JOptionPane.showMessageDialog(null,  "Search reaches "+((forward)?"bottom":"top")+". \""+searchString+"\" not found.", "Find", JOptionPane.PLAIN_MESSAGE); 
			}
		}		
		return result;
	}
	
	private TreePath locateNode(JTree tree, Vector<FileEntry> entries, String name, boolean suppress){
		return locateNode(tree, entries, name, 0, true, suppress);
	}
	
	private TreePath findNextNode(JTree tree, Vector<FileEntry> entries, String name, boolean suppress){
		int current = entries.indexOf(tree.getLastSelectedPathComponent());
		if(current < entries.size()-1){
			return this.locateNode(tree, fileEntry, name, current+1, true, suppress);
		}else{
			return null;
		}
	}
	
	private TreePath findPreviousNode(JTree tree, Vector<FileEntry> entries, String name, boolean suppress){
		int current = entries.indexOf(tree.getLastSelectedPathComponent());
		if(current > 0){
			return this.locateNode(tree, fileEntry, name, current-1, false, suppress);
		}else{
			return null;
		}
	}	
	
	private void batchSearch(JTree tree, Vector<FileEntry> entries, String find, String replace, boolean sameDir){
		Vector<FileEntry> listA = new Vector<FileEntry>();
		Vector<FileEntry> listB = new Vector<FileEntry>();
		TreePath current = null;
		FileEntry parent;
		String temp;
		
		// Hide
		tree.setExpandsSelectedPaths(false);
		
		// Search for "find"
		tree.setSelectionPath(new TreePath(entries.elementAt(0).getPath()));
		while((current = findNextNode(tree, entries, find, true)) != null){
			tree.setSelectionPath(current);
			listA.add((FileEntry) tree.getLastSelectedPathComponent());
			//System.out.println(tree.getLastSelectedPathComponent());
		}
		
		// Fill "replace" vector
		listB.setSize(listA.size());
		
		// Search for "replace"
		for(int i=0; i<listA.size(); i++){
			tree.setSelectionPath(new TreePath(entries.elementAt(0).getPath()));
			temp = listA.elementAt(i).getName().toLowerCase().replaceAll(find.toLowerCase(), replace.toLowerCase());
			parent = listA.elementAt(i).getParent();
			
			while((current = findNextNode(tree, entries, temp, true)) != null){
				tree.setSelectionPath(current);
				if( !sameDir || (sameDir && ((FileEntry) tree.getLastSelectedPathComponent()).getParent() == parent)){
					if(tree.getLastSelectedPathComponent() != listA.elementAt(i)){
						listB.setElementAt((FileEntry) tree.getLastSelectedPathComponent(), i);
						break;
					}
				}
			}
		}
		
		// Clean up list
		for(int i=0; i<listA.size(); i++){
			if(listB.elementAt(i) == null){
				listA.removeElementAt(i);
				listB.removeElementAt(i);
				i--;
			}
		}
		
		// Show
		tree.setExpandsSelectedPaths(true);
		
		entryCopier.batchInit(listA, listB);
		// Debug
//		for(int i=0; i<listA.size(); i++){
//			System.out.println((i+1)+" "+listA.elementAt(i)+"\t\t"+listB.elementAt(i));
//		}
	}
	
	// Table Related
	private void displayInTable(FileEntry node, JTable table){
		// Path
        FileEntry curParent = node;
        StringBuffer strBuff = new StringBuffer(0);
        while(curParent != null && !curParent.isRoot()){
	      	strBuff.insert(0, "\\"+curParent.getName());
        	curParent = curParent.getParent();
        }
    	
    	// Display values
    	table.setValueAt(fileEntry.indexOf(node),0,1);
    	table.setValueAt((strBuff.length() != 0)?strBuff:"\\",1,1);
    	table.setValueAt(node.getName(),2,1);
		table.setValueAt(node.getStrOffset(),3,1);
		table.setValueAt(Integer.toHexString(((node.getType())?node.getOffset():node.getOffset()*4)),4,1);
		table.setValueAt(node.getSize(),5,1);
	}
	
	private void clearTable(){
		for(int i=0; i<5; i++){
			table.setValueAt(null,i,1);
		}
	}
	
	// Event Handlers
	public void valueChanged(TreeSelectionEvent e){		// onSelect TreeNode
		FileEntry node = (FileEntry)tree.getLastSelectedPathComponent();

        if (node == null) return;
		
		// Clear Table Selection & editing
		if (table.getCellEditor() != null) {
			table.getCellEditor().cancelCellEditing();
		}
		table.clearSelection();
        
        displayInTable(node, table);
	}
	
	public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
		// File Menu
		if(source == exitJava){
			System.exit(0);
		}else if(source == openFile){
			if(loadFile.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
				loadFile(loadFile.getSelectedFile());
			}
		}else if(source == saveFile){
			if(fileEntry != null && loadFile.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION){
				File saveTo = loadFile.getSelectedFile();
				writeEntry(saveTo);
				fstFile = saveTo;
				frame.setTitle(DEFAULT_TITLE+" - "+fstFile.getName());
			}
		}else if(source == reloadFile){
			// Save current selection
			int current = fileEntry.indexOf(tree.getLastSelectedPathComponent());
			
			// Reload file
			loadFile(fstFile);
			
			// Scroll previous selection to view
			if(current != -1){
				TreePath reloadPath = new TreePath(fileEntry.elementAt(current).getPath());
				tree.scrollPathToVisible(reloadPath);
				tree.setSelectionPath(reloadPath);
			}
		}else if(source == closeFile){
			loadFile(new File(""));
			tree.setVisible(false);
			treeView.updateUI();
			frame.setTitle(DEFAULT_TITLE);
		
		// Edit Menu
		}else if(source == findNode && fileEntry.size() != 0){
			TreePath node;
			searchString = JOptionPane.showInputDialog("Find:", searchString);
			if(searchString != null){		// Search if OK, do nth if cancel
				node = locateNode(tree, fileEntry, searchString, false);
				if(node != null){
					tree.scrollPathToVisible(node);
					tree.setSelectionPath(node);
				}
			}
		}else if(source == findNext && fileEntry.size() != 0 && searchString != null){
			TreePath node;
			node = findNextNode(tree, fileEntry, searchString, false);
			if(node != null){
				tree.scrollPathToVisible(node);
				tree.setSelectionPath(node);
			}
		}else if(source == findPrev && fileEntry.size() != 0 && searchString != null){
			TreePath node;
			node = findPreviousNode(tree, fileEntry, searchString, false);
			if(node != null){
				tree.scrollPathToVisible(node);
				tree.setSelectionPath(node);
			}
		}else if(source == batchFind){
			//batchSearch(tree, fileEntry, "_JA", "_GE", false);	// TEST
			if(fileEntry.size() != 0){
				String find = JOptionPane.showInputDialog("Find files with pattern");
				if(find != null){
					String replace = JOptionPane.showInputDialog("Replace by files with pattern");
					if(replace != null){
						System.out.println(find+" "+replace);
						batchSearch(tree, fileEntry, find, replace, false);
					}
				}
			}
		// View Menu
		}else if(source == expandTree){
			expandAll(tree, true);
		}else if(source == collapseTree){
			expandAll(tree, false);
			tree.expandRow(0);
		
		// Popup Menu
		}else if(source == treePopupCopy){
			entryCopier.copy((FileEntry)tree.getLastSelectedPathComponent());
		}else if(source == treePopupPasteOffset){
			entryCopier.paste((FileEntry)tree.getLastSelectedPathComponent(),true,false);
			displayInTable((FileEntry)tree.getLastSelectedPathComponent(), table);
		}else if(source == treePopupPasteSize){
			entryCopier.paste((FileEntry)tree.getLastSelectedPathComponent(),false,true);
			displayInTable((FileEntry)tree.getLastSelectedPathComponent(), table);
		}else if(source == treePopupPasteBoth){
			entryCopier.paste((FileEntry)tree.getLastSelectedPathComponent(),true,true);
			displayInTable((FileEntry)tree.getLastSelectedPathComponent(), table);
		}
    }
    
    public void menuCanceled(MenuEvent e){
    	
    }
	
	public void menuDeselected(MenuEvent e){
	}

 	public void menuSelected(MenuEvent e){
 	}
        
    public void itemStateChanged(ItemEvent e) {
    }
	
	// main
	public static void main(String[] args){
        FstReader fstReader = new FstReader();
		fstReader.showGUI();
    }
    
    // Classes
    class PopupListener extends MouseAdapter {
        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
                if(!((FileEntry)tree.getLastSelectedPathComponent()).getType()){
	                popup.show(e.getComponent(),
					e.getX(), e.getY());
                }
            }
        }
    }

}