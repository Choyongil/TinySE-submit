package edu.hanyang.submit;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import edu.hanyang.indexer.BPlusTree;

class Node{
	
	// leaf_check : leaf_node = 1  &  non_leaf_node = 0		default : 1
	// index : node's address(0, 52, 104 ...)				default : 0
	// parent_index : node's parent address(0, 52, 104 ...) default : -1
	// num_key : number of key which node has				default : 0
	
   int leaf_check;      
   int index;        
   int parent_index;
   int num_key;      
   
   
    // list of key which have node has
    // list of value which have node has
   
   List<Integer> key = new ArrayList<Integer>();		
   List<Integer> value = new ArrayList<Integer>();
   
   
   public Node(){
      this.leaf_check = 1;
      this.index = 0;
      this.parent_index = -1;
      this.num_key = 0;
   }
}

public class TinySEBPlusTree implements BPlusTree{

   RandomAccessFile file;		// metafile
   RandomAccessFile file2;		// savefile(=filepath)
   int blocksize;				// blocksize
//   int nblocks;					
   int root_index;				// root node 의 위치 값
   int fanout;					// maximum num of pointer 갯수
   int split_num;				// num of key which origin node has when split
   
   @Override
   public void close() {

      try {
         this.file2.seek(0);
         this.file2.writeInt(this.root_index);
         this.file.close();
         this.file2.close();
      } 
      catch(Exception e) {
         System.out.println(e);
      }
   }
   
   public Node check_index(int index) {
      
      Node check_node = new Node();
      
      byte[] byte_buf = new byte[this.blocksize];
      ByteBuffer buf;
      
      try {
         if (file.length() == 0) {
            
            check_node.index = this.root_index;
            
         } 
         else {
            
            file.seek(index);
            file.read(byte_buf);
            buf = ByteBuffer.wrap(byte_buf);
            
            check_node.leaf_check = buf.getInt();
            check_node.index = index;
            check_node.parent_index = buf.getInt();
            check_node.num_key = buf.getInt();
         
            for ( int i = 0 ; i < check_node.num_key ; i++) {
               
               check_node.key.add(buf.getInt());
               check_node.value.add(buf.getInt());
            }

            if ( check_node.leaf_check == 0) {
               check_node.value.add(buf.getInt());
            }
         }
         
      } catch(Exception e) {
         System.out.println(e);
      }
      
      return check_node;
      
   }
   
   public void write_index(Node write_node) {
      
      try {
         byte[] byte_buf = new byte[this.blocksize];
         ByteBuffer buf = ByteBuffer.wrap(byte_buf);

         List<Integer> keys = write_node.key;
         List<Integer> values = write_node.value;
         
         // buf 에 node의 변수 순서대로 저장
         buf.putInt(write_node.leaf_check);
         buf.putInt(write_node.parent_index);
         buf.putInt(write_node.num_key);
         
         // buf 에 key, value 값을 순서대로 저장
         for (int i = 0 ; i < write_node.num_key ; i++) {
            buf.putInt(keys.get(i));
            buf.putInt(values.get(i));
         }
         
         // leaf_node 가 아니라면 value 값(non-leaf_node 일 땐 pointer 값)이 key 갯수보다 하나 더 존재
         if ( write_node.leaf_check == 0) {
            buf.putInt(values.get(write_node.num_key));
         }
         
         this.file.seek(write_node.index);
         this.file.write(byte_buf);
         
      } catch (IOException e) {
         System.out.println(e);
      }
      
            
   }

   @Override
   public void insert(int key, int val) {
      
      Node insert_node = check_index(this.root_index);
      
      while( insert_node.leaf_check == 0 ) {
         if ( insert_node.key.get(0) > key) {
            insert_node = check_index(insert_node.value.get(0));
         }
         else if ( insert_node.key.get(insert_node.num_key - 1) < key) {         // root_node 의 마지막 key 보다 클 때 
            insert_node = check_index(insert_node.value.get(insert_node.num_key));
         }
         else {
//        	int s_i = insert_node.num_key / 2;
//    		if (insert_node.key.get(s_i) < key && insert_node.key.get(s_i+1) > key ) {
//              insert_node = check_index(insert_node.value.get(s_i+1));
////              break;
//           }
//    		else if( insert_node.key.get(s_i) > key ) {
//    			for( int i = 0 ; i < s_i ; i++) {
//                  if (insert_node.key.get(i) < key && insert_node.key.get(i+1) > key ) {
//                     insert_node = check_index(insert_node.value.get(i+1));
//                     break;
//                  }
//               }
//    		}
//    		else {
//    			for( int i = s_i+1 ; i < insert_node.num_key ; i++) {
//                  if (insert_node.key.get(i) < key && insert_node.key.get(i+1) > key ) {
//                     insert_node = check_index(insert_node.value.get(i+1));
//                     break;
//                  }
//               }
//    		}
        	
            for( int i = 0 ; i < insert_node.num_key ; i++) {
               if (insert_node.key.get(i) < key && insert_node.key.get(i+1) > key ) {
                  insert_node = check_index(insert_node.value.get(i+1));
                  break;
               }
            }
         }
      }
      
      if (insert_node.num_key == 0) {
         insert_node.key.add(key);
         insert_node.value.add(val);
         insert_node.num_key++;
      }
      else {
         if ( insert_node.key.get(insert_node.num_key - 1) < key) {
            insert_node.key.add(insert_node.num_key, key);
            insert_node.value.add(insert_node.num_key, val);
            insert_node.num_key++;
         }
         else if (insert_node.key.get(0) > key) {
            insert_node.key.add(0, key);
            insert_node.value.add(0, val);
            insert_node.num_key++;
         }
         else {
            for( int i = 0 ; i < insert_node.num_key ; i++) {
               if (insert_node.key.get(i) <= key && insert_node.key.get(i+1) > key) {
                  insert_node.key.add(i+1, key);
                  insert_node.value.add(i+1, val);
                  insert_node.num_key++;
                  break;
               }
            }
         }
      }
      
      write_index(insert_node);
      
      if (insert_node.num_key == this.fanout) {
         
         split(insert_node);
         
      }
      
//      System.out.println("root_index : " + this.root_index);
//      
//      
//      for (int i=0; i<check_split + 5; i++) {
//            System.out.println("주소값" + i*52 + " 일 때의 key : " + check_index(i * 52).key);
//            System.out.println("주소값" + i*52 + " 일 때의 value : " + check_index(i * 52).value);
//            System.out.println("주소값" + i*52 + " 일 때의 parent_node : " + check_index(i * 52).parent_index);
//            System.out.println("주소값" + i*52 + " 일 때의 leaf node 인가 : " + check_index(i * 52).leaf_check);
//            System.out.println("");
//         }
//         System.out.println(check_split);
////         
//         System.out.println("");
//         System.out.println("");
   }
   
   
   
   
   public Node split(Node input_node) {

      Node parent_node = new Node();
      Node origin_node;
      Node new_node = new Node();
      
      List<Integer> keys = input_node.key;
//      System.out.println(keys);
      List<Integer> values = input_node.value;
//      System.out.println(values);
      
      try {
         
         origin_node = check_index(input_node.index);
         new_node.index = (int)this.file.length();
         
         if (input_node.parent_index == -1) {               // parent_node 가 없을 때
            origin_node.num_key = 0;
            origin_node.key.clear();
            origin_node.value.clear();
            new_node.value.clear();
            
            for (int i = 0 ; i < this.split_num ; i++) {
               origin_node.key.add(keys.get(i));
               origin_node.value.add(values.get(i));
               origin_node.num_key++;
            }
            for (int i = this.split_num ; i < input_node.num_key ; i++) {
               new_node.key.add(keys.get(i));
               new_node.value.add(values.get(i));
               new_node.num_key++;
            }
            
            parent_node.index = (int)this.file.length() + this.blocksize;
            
            origin_node.parent_index = parent_node.index;
            new_node.parent_index = parent_node.index;
            
            parent_node.leaf_check = 0;
            parent_node.key.add(new_node.key.get(0));
            parent_node.value.add(origin_node.index);
            parent_node.value.add(new_node.index);
            parent_node.num_key++;
            
            if (parent_node.parent_index == -1) {
               this.root_index = parent_node.index;
            }
            write_index(origin_node);
            write_index(new_node);
            write_index(parent_node);
            
         } else {                                    // parent_node 가 있을 때

            parent_node = check_index(input_node.parent_index);
            
            origin_node.parent_index = parent_node.index;
            new_node.parent_index = parent_node.index;
            origin_node.key.clear();
            origin_node.value.clear();
            origin_node.num_key = 0;
            for (int i = 0 ; i < this.split_num ; i++) {
               origin_node.key.add(keys.get(i));
               origin_node.value.add(values.get(i));
               origin_node.num_key++;
            }
            for (int i = this.split_num ; i < input_node.num_key ; i++) {
               new_node.key.add(keys.get(i));
               new_node.value.add(values.get(i));
               new_node.num_key++;
            }
            if ( parent_node.key.get(parent_node.num_key - 1) < new_node.key.get(0)) {
               parent_node.key.add(parent_node.num_key, new_node.key.get(0));
               parent_node.value.add(parent_node.num_key+1, new_node.index);
               parent_node.num_key++;
            } 
            else if (parent_node.key.get(0) > new_node.key.get(0)){
               parent_node.key.add(0, new_node.key.get(0));
               parent_node.value.add(1, new_node.index);
               parent_node.num_key++;
            }
            else {                  
               for ( int i = 0 ; i < parent_node.num_key ; i++ ) {
                  if ( parent_node.key.get(i) < new_node.key.get(0) && parent_node.key.get(i+1) > new_node.key.get(0)) {
                     
                     parent_node.key.add(i+1, new_node.key.get(0));
                     parent_node.value.add(i+2, new_node.index);
                     parent_node.num_key++;
                     
                     break;
                  }
               }
            }
            write_index(origin_node);
            write_index(new_node);
            
            if (parent_node.num_key == this.fanout) {
               int temp_k = parent_node.key.get(this.split_num-1);
               parent_node.key.remove(this.split_num-1);
               parent_node.num_key--;
               write_index(parent_node);
               parent_node = non_leaf_split(parent_node, temp_k);      
            }                  
            write_index(parent_node);
         }
         

         return parent_node; 
         
      } catch (IOException e) {
         System.out.println(e);
      }
      
      return null;
      
   }
   
   public Node non_leaf_split(Node node, int key) {

      List<Integer> values = node.value;
      
      Node parent = new Node();
      Node origin_node = new Node();
      Node new_node = new Node();
      
      try {
         if (node.parent_index == -1 ) {
            
            parent.index = (int)this.file.length()+this.blocksize;
            
            this.root_index = parent.index;
            
            parent.key.add(key);
            parent.leaf_check = 0;
            parent.num_key++;
            parent.value.add(node.index);
            parent.value.add(parent.index - this.blocksize);
            
            origin_node = check_index(node.index);
            new_node.index = parent.value.get(1);
            
            origin_node.key.clear();
            origin_node.value.clear();
            origin_node.num_key = 0;
            
            origin_node.parent_index = parent.index;
            new_node.parent_index = parent.index;
            
            origin_node.leaf_check = 0;
            new_node.leaf_check = 0;
            
            for (int i = 0 ; i < this.split_num-1 ; i++) {
               origin_node.key.add(node.key.get(i));
               origin_node.value.add(values.get(i));
               origin_node.num_key++;
            }
            origin_node.value.add(values.get(this.split_num-1));
            
            for (int i = this.split_num-1 ; i < this.fanout - 1 ; i++) {
               new_node.key.add(node.key.get(i));
               new_node.num_key++;
            }
            
            for (int i = this.split_num-1 ; i < this.fanout - 1 ; i++) {
                new_node.value.add(values.get(i+1));
             }
            
            new_node.value.add(values.get(this.fanout));
            
         }
         
         else {
            
            parent = check_index(node.parent_index);
            
            origin_node = check_index(node.index);            
            new_node = check_index((int)this.file.length());
            new_node.parent_index = origin_node.parent_index;
            
            origin_node.num_key = 0;
            origin_node.key.clear();
            origin_node.value.clear();
            new_node.value.clear();
            new_node.leaf_check = 0;

            for (int i = 0 ; i < this.split_num-1 ; i++) {
                origin_node.key.add(node.key.get(i));
                origin_node.value.add(values.get(i));
                origin_node.num_key++;
			}
			origin_node.value.add(values.get(this.split_num-1));
			 
			for (int i = this.split_num-1 ; i < this.fanout - 1 ; i++) {
				new_node.key.add(node.key.get(i));
			    new_node.num_key++;
			}
			 
			for (int i = this.split_num-1 ; i < this.fanout - 1 ; i++) {
			     new_node.value.add(values.get(i+1));
			}
			 
			new_node.value.add(values.get(this.fanout));
            
            if (parent.key.get(0) > key) {
               parent.key.add(0, key);
               parent.num_key++;
               parent.value.add(1,new_node.index);
            }
            else if (parent.key.get(parent.num_key - 1) < key) {
               parent.key.add(parent.num_key, key);
               parent.value.add(parent.num_key + 1,new_node.index);
               parent.num_key++;
            }
            else {
               for(int i = 0 ; i < parent.num_key - 1 ; i++) {
                  if(parent.key.get(i) < key && parent.key.get(i+1) > key) {
                     parent.key.add(i+1, key);
                     parent.num_key++;
                     parent.value.add(i+2, new_node.index);
                     break;
                  }
               }
            }
         }
         
         write_index(origin_node);
         write_index(new_node);
         
         for ( int i = 0 ; i <= new_node.num_key ; i++) {

            Node temp_node = new Node();
            temp_node = check_index(new_node.value.get(i));
            temp_node.parent_index = new_node.index;
            write_index(temp_node);
         }
         
         if (parent.num_key == this.fanout) {
            int temp_k = parent.key.get(this.split_num-1);
            parent.key.remove(this.split_num-1);
            parent.num_key--;
            write_index(parent);
            parent = non_leaf_split(parent, temp_k);
         }
         
         write_index(parent);
         
      } catch (IOException e) {
         System.out.println(e);
      }
      
      return parent;
   }
   
   @Override
   public void open(String metafile, String filepath, int blocksize, int nblocks) {

      this.blocksize = blocksize;
      this.fanout = ((blocksize / 4) - 3) / 2;   // 4*(max_num_keys + 3) <= blocksize
      
      try {
    	  
    	  if (this.fanout % 2 == 1) {
    		   this.split_num = this.fanout / 2 + 1;
    	   }
    	   else {
    		   this.split_num = this.fanout / 2;
    	   }
    	  
         this.file = new RandomAccessFile(filepath, "rw");
         this.file2 = new RandomAccessFile(metafile, "rw");
         
         if(file.length() == 0) {
            this.root_index = 0;
            this.file2.writeInt(root_index);
         }
         else {
            
            this.root_index = this.file2.readInt();
         }
      }
      catch(Exception e) {
         System.out.println(e);
      }
   }
   
   @Override
    public int search(int key) {
      
      Node search_node = check_index(this.root_index);
      while(search_node.leaf_check != 1) {
         if ( search_node.key.get(0) > key) {
            search_node = check_index(search_node.value.get(0));
         }
         else if( search_node.key.get(search_node.num_key - 1) <= key ) {
            search_node = check_index(search_node.value.get(search_node.num_key));
         }
         else {
//        	 int s_i = search_node.num_key / 2;
//         	
//     		if (search_node.key.get(s_i) <= key && search_node.key.get(s_i+1) > key ) {
//     			search_node = check_index(search_node.value.get(s_i+1));
//               break;
//            }
//     		else if( search_node.key.get(s_i) > key ) {
//     			for(int i = 0 ; i < s_i ; i++) {
// 	               if (search_node.key.get(i) <= key && key < search_node.key.get(i+1)) {
// 	                  search_node = check_index(search_node.value.get(i+1));
// 	                  break;
// 	               }
//     			}
//     		}
//     		else {
//     			for(int i = s_i+1 ; i < search_node.num_key ; i++) {
//  	               if (search_node.key.get(i) <= key && key < search_node.key.get(i+1)) {
//  	                  search_node = check_index(search_node.value.get(i+1));
//  	                  break;
//  	               }
//      			}
//     		}
         	
            for (int i = 0 ; i < search_node.num_key ; i ++) {
               if (search_node.key.get(i) <= key && key < search_node.key.get(i+1)) {
                  search_node = check_index(search_node.value.get(i+1));
                  break;
               }
            }
         }
      }
      if (search_node.key.get(0) > key || search_node.key.get(search_node.num_key - 1) < key) {
         return -1;
      }
      for (int i = 0 ; i < search_node.num_key ; i++) {
         if ( search_node.key.get(i) == key) {
            return search_node.value.get(i);
         }
      }
      
      return -1;
   }
}