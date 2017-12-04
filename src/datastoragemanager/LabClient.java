package datastoragemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class LabClient {
	
	public static IO io_count = new IO(0, 0, 0, 0, 0, 0);
	public static DSMgr disk_client = new DSMgr(io_count);
	public BMgr buffer_client = new BMgr(disk_client);
	
	/**
	 * 创建文件
	 * @param filename 文件名称
	 * @return 是否创建成功，成功则返回true
	 */
	public static boolean createFile(String fileName) {
		Boolean bool =false;
		File file = new File(fileName + ".dbf");
		try {
			//如果文件不存在，则创建新的文件
			if(!file.exists()) {
				file.createNewFile();
				bool = true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return bool;
	}
	
	public void IOCount() {
		int total = disk_client.io_count.read_count + disk_client.io_count.write_count;
		System.out.println("读page次数：" + disk_client.io_count.read_count);
		System.out.println("写page次数：" + disk_client.io_count.write_count);
		System.out.println("读page命中：" + disk_client.io_count.read_hit);
		System.out.println("写page命中：" + disk_client.io_count.write_hit);
		System.out.println("命中总数：" + (disk_client.io_count.read_hit + disk_client.io_count.write_hit));
		System.out.println("读IO: " + disk_client.io_count.read_io);
		System.out.println("写IO: " + disk_client.io_count.write_io);
		System.out.println("读写总数: " + total);
	}
	/*
	 * 
	 */
	public void test() {
		try {
			int op[] = new int[500000];
			int pageids[] = new int[500000];
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data-5w-50w-zipf.txt")));
			String data = null;
			int op_count = 0;
			//disk_client.io_count.read_count = 0;
			//disk_client.io_count.write_count = 0;
			while((data = br.readLine()) != null) {
				op[op_count] = Integer.parseInt(data.split(",")[0]);
				pageids[op_count] = Integer.parseInt(data.split(",")[1]);
				op_count += 1;
			}
			//disk_client.OpenFile("");
			for(int i = 0; i < op_count; i ++) {
				if(op[i] == 0) {//读page
					disk_client.io_count.read_count += 1;
					int frame = buffer_client.FixPage(pageids[i], 0);
				}
				else {//写page
					disk_client.io_count.write_count += 1;
					int frame = buffer_client.Writebufpage(pageids[i]);
				}
				
				//System.out.println(pageids[i]);
			}
			//System.out.println(op_count);
			//System.out.println(disk_client.io_count.read_count);
			//System.out.println(disk_client.io_count.read_count + disk_client.io_count.write_count);
			//for(int j = 0; j < buffer_client.lru.pageids.length; j ++) {
				//System.out.println(buffer_client.lru.pageids[j]);
			//}
			//lru队列全是-1？？问题很大
			//System.out.println("shit" + buffer_client.lru.pageids[0]);
			br.close();
			IOCount();
			buffer_client.WriteDirtys();
			for(int i = 0; i < buffer_client.DEFBUFSIZE; i ++) {
				if(buffer_client.ftop[i] != -1) {
					int page = buffer_client.ftop[i];
					buffer_client.RemoveBCB(page);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		LabClient project = new LabClient();
		createFile("data");
		disk_client.OpenFile("data.dbf");
		project.test();
		disk_client.CloseFile();
	}
}
