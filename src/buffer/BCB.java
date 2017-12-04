package buffer;

public class BCB {
	public BCB(int page_id2, int frame_id2, boolean b, int i, boolean c) {
		page_id = page_id2;
		frame_id = frame_id2;
		latch = b;
		count = i;
		dirty = c;
		// TODO Auto-generated constructor stub
	}
	public int page_id;
	public int frame_id;
	public boolean latch;
	public int count;
	public boolean dirty;
}
