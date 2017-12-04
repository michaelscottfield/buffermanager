package datastoragemanager;

//import buffer.Bframe;

public class DSMgr {
	public IO io_count;
	public DSMgr(IO io_param) {
		io_count = io_param;
	}
	public int OpenFile(String filename) {
		
		return 0;
	}
	public int CloseFile() {
		return 0;
		
	}
	public void ReadPage(int page_id) {
		io_count.read_io += 1;
		
	}
	public int WritePage(int frame_id) {
		io_count.write_io += 1;
		return 0;
		
	}
	public int Seek(int offset, int pos) {
		return 0;
		
	}
	
	public void IncNumPages() {
		
	}
	
	public int GetNumPages() {
		return 0;
		
	}
	
	public void SetUse(int index, int use_bit) {
		
	}
	
	public boolean GetUse(int index) {
		return true;
		
	}
}
