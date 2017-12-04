package datastoragemanager;

public class LRU {
	//考虑如何初始化LRU队列
	public int pageids[] = new int[1024];
	public LRU() {
		for(int i = 0; i < 1024; i ++) {
			pageids[i] = -1;
		}
	}
	public void MoveToRear(int page_id) {
		if(pageids[0] != -1) {
			if(pageids[0] == page_id) {
				InsertRear(DeleteHead());
			}
			else {
				int i;
				for(i = 1; i < pageids.length; i ++) {
					if(pageids[i] == -1 || pageids[i] == page_id) {
						break;
					}
				}
				if(i < 1023 && pageids[i] == page_id) {
					if(pageids[i + 1] != -1) {
						for(int j = i; j < pageids.length - 1; j ++) {
							pageids[j] = pageids[j + 1];
						}
						pageids[1023] = -1;
						InsertRear(page_id);
					}
				}
			}
		}
	}
	
	public int DeleteHead() {
		//如果本来就是空的队列该如何处理？？
		/*if(pageids[0] == 2550) {
			for(int k = 0; k < pageids.length; k ++) {
				System.out.println(k + "........." + pageids[k]);
			}
			System.out.println("llllll");
		}*/
		if(pageids[0] == -1) {
			return -1;
		}
		else if(pageids[0] != -1 && pageids[1] == -1) {
			int page = pageids[0];
			pageids[0] = -1;
			
			return page;
			
		}
		else {
			int page = pageids[0];
			for(int i = 0; i < pageids.length - 1; i ++) {
				//if(i >= 1023 && pageids[i + 1] ==  -1) {
					//break;
				//}
				pageids[i] = pageids[i + 1];
			}
			//if(pageids[1023] != -1) {
			pageids[1023] = -1;
			//}
			return page;
		}
	}
	
	public void InsertRear(int page_id) {
		if(pageids[0] == -1) {
			pageids[0] = page_id;
		}
		else {
			for(int i = 0; i  < pageids.length; i ++) {
				if(pageids[i] == -1) {
					pageids[i] = page_id;
					return;
				}
			}
		}
	}
}
