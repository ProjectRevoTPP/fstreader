import javax.swing.tree.*;

public class FileEntry extends DefaultMutableTreeNode{
	private boolean type;
	private int strOffset;
	private int offset;
	private int size;
	private String name;
	private int nameMaxLength;
	
	public FileEntry(boolean type, int strOffset, int offset, int size){
		this(type, strOffset, offset, size, "");
	}
	
	public FileEntry(boolean type, int strOffset, int offset, int size, String name){
		super();
		setType(type);
		setStrOffset(strOffset);
		setOffset(offset);
		setSize(size);
		setName(name);
	}
	
	public boolean getType(){
		return type;
	}
	
	public int getStrOffset(){
		return strOffset;
	}
	
	public int getOffset(){
		return offset;
	}
	
	public int getSize(){
		return size;
	}
	
	public String getName(){
		return name;
	}
	
	public FileEntry getParent(){
		return ((FileEntry)super.getParent());
	}
	
	public void setType(boolean type){
		this.type = type;
	}
	
	public void setStrOffset(int strOffset){
		this.strOffset = strOffset;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public void setName(String name){
		if(this.nameMaxLength == 0){
			this.name = name;
			this.nameMaxLength = name.length();
		}else if(name.length() < this.nameMaxLength){
			this.name = name;
		}else{
			System.out.println("New name exceed max length");
		}
	}
	
	public String toString(){
		return name;
	}
	
	public Object clone(){
		FileEntry clone = (FileEntry) super.clone();
		clone.setType(type);
		clone.setStrOffset(strOffset);
		clone.setOffset(offset);
		clone.setSize(size);
		clone.setName(name);
		return clone;
	}
}