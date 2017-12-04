package datastoragemanager;

import buffer.BCB;
import buffer.Bframe;

public class BMgr {
	public int DEFBUFSIZE = 1024;
	//50000 % 1024 = 48...848
	//需要分配1024*49个存储空间
	public BCB ptof[][];
	public int ftop[];
	public LRU lru;
	public DSMgr storage;
	public Bframe buf[];
	
	public BMgr(DSMgr disk) {
		ptof = new BCB[1024][49];
		ftop = new int[DEFBUFSIZE];
		storage = disk;
		for(int i = 0; i < DEFBUFSIZE; i ++) {
			ftop[i] = -1;
			ptof[i] = null;
		}
		lru = new LRU();
		
	}
	/*
	 * ftop和ptof和lru.pageids是否保持一致
	 */
	public int FixPage(int page_id, int prot) {
		//System.out.println(page_id); 8550
		//if(page_id == 2550) System.out.println("sssssssss");
		
		//for(int i = 0; i < lru.pageids.length; i ++) {
			//System.out.print(lru.pageids[i] + "cvb");
		//}
		//System.out.println();
		BCB blocks[] = ptof[page_id % DEFBUFSIZE];
		//if(blocks != null) System.out.println("buffer is no initial");
		int frame_id = Hash(page_id);
		//System.out.println(frame_id + "," + page_id); //frame_id = -1???
		//frameid为什么这么多都是1023？？？
		//为什么不同的pageid对应到102？？？
		if(frame_id == -1) {//page不在frame里面
			//System.out.println("...");
			int freeframes = NumFreeFrames();
			//freeframes=0这里有问题！！！
			//System.out.println(freeframes);
			if(freeframes == -1) {
				//System.out.println("shit");
				//为什么freeframe一直是有的，
				//System.out.println(page_id);
				SelectVictim();
				freeframes = NumFreeFrames();
			}
			/*for(int i =0; i < lru.pageids.length; i ++) {
				if(lru.pageids[i] == -1) break;
				if(Hash(lru.pageids[i]) == -1) {//为什么会输出这么多fewefwefwefe.......
					System.out.println("fewefwefwefe");
				}
			}*/
//			/if(freeframes == -1) System.out.println("unbeliveable");
			lru.InsertRear(page_id);
			//System.out.println("insertrear:" + page_id);
			//if(page_id == 2550) System.out.println("sssssssss");
			storage.ReadPage(page_id);//page只能在disk里面读，所以readio++
			blocks = ptof[page_id % DEFBUFSIZE];
			//if(blocks == null) System.out.println("no blocks");//结果输出了no blocks 
			if(blocks == null) {
				//ptof[page_id % DEFBUFSIZE][0] = new BCB();
				//if(ptof[page_id % DEFBUFSIZE] == null) System.out.println("shitshit"); 成功输出shitshit
				//ptof[page_id % DEFBUFSIZE] = new BCB[49];
				//for(int j = 1; j < 49; j ++) {
					//if(ptof[page_id % DEFBUFSIZE][j] == null) System.out.println(j);
					//ptof[page_id % DEFBUFSIZE][j].page_id = -1;
				//}
				//System.out.println(page_id % DEFBUFSIZE); //输出358
				//System.out.println(ptof.length); //输出1024
				//if(ptof[page_id % DEFBUFSIZE] == null) System.out.println("shit");
				ptof[page_id % DEFBUFSIZE] = new BCB[49];
				//System.out.println(ptof[page_id % DEFBUFSIZE].length);
				ptof[page_id % DEFBUFSIZE][0] = new BCB(page_id, 1024 - freeframes, true, 0, false);
				//if(ptof[page_id % DEFBUFSIZE][1] == null) System.out.println("fsdffsdfd");
				//System.out.println(page_id + "," + freeframes); why is freeframes always 1023...
			}
			else {
				//if(blocks[0] == null) System.out.println("nobody is aaa");
				//System.out.println("fuck"); //没有进入这里？？？
				//lru
				int i;
				for(i = 0; i < blocks.length; i ++) {
					if(blocks[i] == null || blocks[i].page_id < 0) {
						break;
						//System.out.println(blocks[i].page_id);
						//blocks[i - 1] = blocks[i];
					}
				}
				//blocks[i] = new BCB(page_id, frame_id, true, 0, false);
				//System.out.println(ptof[page_id % DEFBUFSIZE].length); //输出1
				//System.out.println(i);
				ptof[page_id % DEFBUFSIZE][i] = new BCB(page_id, 1024 - freeframes, true, 0, false);
				//ftop[frame_id] = page_id;
				
			}
			//if(freeframes == -1) System.out.println("freefree");
			//System.out.println(freeframes);
			//if(freeframes == -1) System.out.println("patrick");
			ftop[1024 - freeframes] = page_id;
			//System.out.println("::" + (1024 - freeframes) + "," + page_id);
			frame_id = 1024 - freeframes;
			//lru.InsertRear(page_id);
		}
		else {
			//System.out.println(",,," + page_id + ",," + frame_id);
			storage.io_count.read_hit += 1;
			
			lru.MoveToRear(page_id);//表示刚刚使用过
			int i;
			//if(blocks[0] == null) System.out.println(frame_id); //输出414????
			for(i = 0; i < blocks.length; i ++) {
				//if(blocks[i] == null) System.out.println(i + "ccccccccccccc");
				//blocks[0] = null?????
				if(blocks[i].page_id == page_id) {
					blocks[i].count = 0;
					blocks[i].latch = true;
					return blocks[i].frame_id;
				}
				else {
					
				}
			}
		}
		
		return frame_id;
		
	}
	/*
	 * fixnewpage在本实验不需要，因为trace文件里面的页全都在磁盘里面
	 */
	public int FixNewPage(int page_id) {//如果要写的page在磁盘中是不存在的，那么生成新的page并放置缓冲区中，并设置该frame的dirty为true
		//System.out.println("fwefewfweew");
		BCB blocks[] = ptof[page_id % DEFBUFSIZE];
		int frame_id = NumFreeFrames();
		if(frame_id == -1) {
			SelectVictim();
			frame_id = NumFreeFrames();
		}
		lru.InsertRear(page_id);
		
		blocks = ptof[page_id % DEFBUFSIZE];
		if(blocks[0] == null) {
			ptof[page_id % DEFBUFSIZE][0] = new BCB(page_id, frame_id, true, 0, false);
		}
		else {
			int i;
			for(i = 1; i < blocks.length; i ++) {
				if(blocks[i] != null) {
					blocks[i - 1] = blocks[i];
				}
			}
			blocks[i] = new BCB(page_id, frame_id, true, 0, false);
		}
		ftop[frame_id] = page_id;
		SetDirty(frame_id);
		return frame_id;
	}
	/*
	 * 由于本实验限制frame只能放一个页，所以unfixpage没用
	 */
	public int UnfixPage(int page_id)//这个函数无用
	{
	    //BCB * f_ptr = ptof[page_id % DEFBUFSIZE];
		BCB blocks[] = ptof[page_id % DEFBUFSIZE];
		for(int i = 0; i < blocks.length; i ++) {
			if(blocks[i] == null || blocks[i].page_id == -1) {
				break;
			}
			if(blocks[i].page_id == page_id) {
				if(blocks[i].latch) {
					blocks[i].count += 1;
					if(blocks[i].count > 0) {
						blocks[i].latch = false;
					}
				}
				else {
					blocks[i].count += 1;
				}
				return blocks[i].frame_id;
			}
			else {
				continue;
			}
		}
		
		return -1;
	}
	
	public int NumFreeFrames() {
		// TODO Auto-generated method stub
		int count = 0;
		//System.out.println("this function has been done");
		//System.out.println(ftop.length); //输出1024，没有问题
		while((count < DEFBUFSIZE) && (ftop[count] != -1)) {
			//System.out.println("have free frame");
			count += 1;
		}
		if(count == 1024) {
			//System.out.println("shit");
			return -1;
		}
		else {
			return (1024 - count);
		}
	}
	
	public int Writebufpage(int page) {
		if(Hash(page) == -1) {
			if(storage.GetUse(page)) {
				FixPage(page, 0);
				//System.out.println(page); //page=8550
				//System.out.println(FindFrame(page).frame_id);
				//frame_id为什么会是1024????
				SetDirty(FindFrame(page).frame_id);				
			}
			else {
				//System.out.println("fneuiwfniueweinfwu");
				FixNewPage(page);
			}
		}
		else {
			storage.io_count.write_hit += 1;
			lru.MoveToRear(page);//队尾刚刚被用过，不会被删除。。
			SetDirty(FindFrame(page).frame_id);
		}
		
		return FindFrame(page).frame_id;
	}

	public BCB FindFrame(int page_id) {
		//pageid为什么是-1？？？
		if(page_id == -1) return null;
		BCB bcblocks[] = ptof[page_id % DEFBUFSIZE];
		//for(BCB bcblock : bcblocks) {
		for(int i = 0; i < bcblocks.length; i ++) {
			if(bcblocks[i] == null) {
				continue;
			}
			if(bcblocks[i].page_id == page_id) {
				//System.out.println(";;");
				return bcblocks[i];
			}
		}
		return null;
	}
	
	public int Hash(int page_id) {
		BCB bcblocks[] = ptof[page_id % DEFBUFSIZE];
		//ptof一开始都是Null
		//System.out.println("shit"); hash函数为何返回-1????
		//if(bcblocks == null) System.out.println("buffer is null");
		//blocks全为空？？？
		//for(BCB bcblock : bcblocks) {
		if(bcblocks == null) return -1;
		if(bcblocks[0] == null) return -1;
		for(int i = 0; i < bcblocks.length; i ++) {
			if(bcblocks[i] != null && bcblocks[i].page_id == page_id) {
				//System.out.println("frame found" + bcblocks[i].frame_id + "," + page_id + "," + bcblocks[i].page_id); //
				//为什么全是frame1023....
				//到ptof设置的地方进行检查
				return bcblocks[i].frame_id;
			}
		}
		return -1;
	}
	
	public int SelectVictim() {
		//需要用到LRU队列
		int del_page = lru.DeleteHead();
		//if(Hash(del_page) == -1) System.out.println("problem");
		//if(del_page == 2550) System.out.println("dddddddddd");
		//System.out.println(del_page);
		//if(delpage == -1)
		//if(FindFrame(del_page) == null) System.out.println("no frames found" + del_page); //print 2550???
		if(FindFrame(del_page).dirty == true) {
			//System.out.println("fehoiwqoi");
			//为什么从来没执行到这里
			storage.WritePage(FindFrame(del_page).frame_id);
			//FindFrame(del_page).dirty = false;
		}
		RemoveBCB(del_page);
		return 0;
	}
	
	
	/*
	 * 
	 */
	public void RemoveBCB(int page_id) {
		BCB blocks[] = ptof[page_id % DEFBUFSIZE];
		//if(blocks[0] == null) System.out.println("fdsaasfioweqjoifew");
		int i;
		for(i = 0; i < blocks.length; i ++) {
			if(blocks[i].page_id == page_id) {
				break;
			}
		}
		//System.out.println(i + "uuuuu");
		//if(ptof[page_id % DEFBUFSIZE][0] == null) System.out.println("..fsdaf.dsafok");
		//System.out.println(blocks[i].frame_id + "dnwqodnqinowwnoid");
		ftop[blocks[i].frame_id] = -1;
		for(; i < blocks.length - 1; i ++) {
			blocks[i] = blocks[i + 1];
		}
		//if(ptof[page_id % DEFBUFSIZE][0] == null) System.out.println("..fsdaf.dsafok");
		//if(blocks[0] == null) System.out.println("..fsdaf.dsafok");
		//System.out.println(blocks.length + "rrrrrrr");
		//blocks的长度错误
		blocks[blocks.length - 1] = new BCB(-1, -1, true, 0, false); 
		/*if(blocks[0].page_id == page_id) {
			ftop[blocks[0].frame_id] = -1;
			//ptof[page_id % DEFBUFSIZE][0] = ptof[page_id % DEFBUFSIZE][1];
			for(int i = 0; i < ptof[page_id % DEFBUFSIZE].length - 1 ; i ++) {
				ptof[page_id % DEFBUFSIZE][i] = ptof[page_id % DEFBUFSIZE][i + 1];
			}
			ptof[page_id % DEFBUFSIZE][ptof[page_id % DEFBUFSIZE].length - 1] = new BCB(-1, -1, false, 0, false); 
			//blocks[0] = blocks[1];//why is blocks null?
			//if(blocks[0] == null) System.out.println("ffs");
			//System.out.println(blocks[0].frame_id);
			
		}
		else {
			for(int i = 1; i < blocks.length; i ++) {
				if(blocks[i] != null && blocks[i].page_id == page_id) {
					if(blocks[i + 1] == null) {
						blocks[i] = null;
						break;
					}
					ftop[blocks[i].frame_id] = -1;
					blocks[i] = blocks[i + 1];
					//System.out.println(i); //i=43
					//if(blocks[i] == null) System.out.println("shit..");
					//System.out.println(blocks[i].frame_id);
					
				}
			}
		}*/
	}
	
	/*
	 * 使用LRU算法从buffer删除一个page
	 */
	public void RemoveLRUEle() {
		boolean delete_tag = false;
		int LRUpage = -1;
		int LRUcount = 0;
		for(int i = 0; i < DEFBUFSIZE; i ++) {
			BCB blocks[] = ptof[i];
			for(int j = 0; j < blocks.length; j ++) {
				if(blocks[j].latch == false) {
					if(blocks[j].count > LRUcount) {
						LRUpage = blocks[j].page_id;
						LRUcount = blocks[j].count;
					}					
				}
				else {
					
				}
			}
		}
		if(delete_tag != true) {
			RemoveBCB(LRUpage);
		}
	}
	
	public void SetDirty(int frame_id) {
		//System.out.println(frame_id + "," + ftop.length); //frame.id = ftop.length = 1024
		//if(ftop[0] == null) System.out.println("shitshit");
		//System.out.println(ftop[frame_id]); ftop[frame_id] = -1???
		FindFrame(ftop[frame_id]).dirty = true;
	}
	
	public void UnsetDirty(int frame_id) {
		FindFrame(ftop[frame_id]).dirty = false;
	}
	
	public void WriteDirtys() {
		for(int i = 0; i < DEFBUFSIZE; i ++) {
			int page = ftop[i];
			if(page != -1) {
				if(FindFrame(page).dirty == true) {
					storage.WritePage(FindFrame(page).frame_id);
					FindFrame(page).dirty = false;
				}
			}
		}
	}
	
	public void PrintFrame(int frame_id) {
		//本实验中frame里面并没有具体的数据
	}
}
