package datastoragemanager;

public class IO {
	int read_count;
	int write_count;
	int read_io;
	int write_io;
	int read_hit;
	int write_hit;
	public IO(int rc, int wc, int ri, int wi, int rh, int wh) {
		read_count = rc;
		write_count = wc;
		read_io = ri;
		write_io = wi;
	}
	public void init() {
		read_count = 0;
		write_count = 0;
		read_io = 0;
		write_io = 0;
		read_hit = 0;
		write_hit = 0;
	}
	
}
