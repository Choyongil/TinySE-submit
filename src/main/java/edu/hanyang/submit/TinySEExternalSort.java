package edu.hanyang.submit;

import java.io.IOException;

import edu.hanyang.indexer.ExternalSort;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
<<<<<<< HEAD
import java.util.PriorityQueue;

import org.apache.commons.lang3.tuple.MutableTriple;
=======
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
import org.apache.commons.lang3.tuple.Triple;
import java.io.File;

public class TinySEExternalSort implements ExternalSort {
<<<<<<< HEAD

	class Tuple implements Comparable<Tuple> {
		
		int index,word_id,doc_id,pos;
		public Tuple(int index, int word_id, int doc_id, int pos){
			this.index = index;
			this.word_id = word_id;
			this.doc_id = doc_id;
			this.pos = pos;
		}
		
		@Override
		public int compareTo(Tuple a){
			if(a.word_id > this.word_id) return -1;
			else if(a.word_id < this.word_id) return 1;
			else{
				if(a.doc_id > this.doc_id) return -1;
				else if(a.doc_id < this.doc_id) return 1;
				else{
					if(a.pos > this.pos) return -1;
					else return 1;
				}
			}
		}
	}

	class TripleSort implements Comparator<Triple<Integer,Integer,Integer>> {
		@Override
		public int compare(Triple<Integer,Integer,Integer> a, Triple<Integer,Integer,Integer> b) {
			if(a.getLeft() > b.getLeft()) return 1;
			else if(a.getLeft() < b.getLeft()) return -1;
			else{
				if(a.getMiddle() > b.getMiddle()) return 1;
				else if(a.getMiddle() < b.getMiddle()) return -1;
				else{
					if(a.getRight() > b.getRight()) return 1;
					else return -1;
				}
			}
		}
	}
	
	public void sort(String infile, String outfile, String tmpdir, int blocksize, int nblocks) throws IOException {

=======
	
	public void sort(String infile, String outfile, String tmpdir, int blocksize, int nblocks) throws IOException {
		
		class Tuple{
			int index,word_id,doc_id,pos;
			public Tuple(int index, int word_id, int doc_id, int pos){
				this.index = index;
				this.word_id = word_id;
				this.doc_id = doc_id;
				this.pos = pos;
			}
		}
		
		class TripleSort implements Comparator<Triple<Integer,Integer,Integer>> {
			@Override 
			public int compare(Triple<Integer,Integer,Integer> a, Triple<Integer,Integer,Integer> b) { 
				if(a.getLeft() > b.getLeft()) return 1;
				else if(a.getLeft() < b.getLeft()) return -1;
				else{
					if(a.getMiddle() > b.getMiddle()) return 1;
					else if(a.getMiddle() < b.getMiddle()) return -1;
					else{
						if(a.getRight() > b.getRight()) return 1;
						else return -1;
					}
				} 
			} 
		}
		class TupleSort implements Comparator<Tuple> {
			@Override 
			public int compare(Tuple a, Tuple b) { 
				if(a.word_id > b.word_id) return 1;
				else if(a.word_id < b.word_id) return -1;
				else{
					if(a.doc_id > b.doc_id) return 1;
					else if(a.doc_id < b.doc_id) return -1;
					else{
						if(a.pos > b.pos) return 1;
						else return -1;
					}
				} 
			} 
		}
		
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
		File dir = new File(tmpdir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(infile),blocksize));
<<<<<<< HEAD
		DataOutputStream run_writer;
		ArrayList<MutableTriple<Integer, Integer, Integer>> runs = new ArrayList<MutableTriple<Integer, Integer, Integer>>();
		int word_id, doc_id, pos;
		int run_cnt = 1;
		int pass_cnt = 1;
		int run_control = 70;
		while(input.available() != 0){
			if( input.available() > nblocks * run_control * (Integer.SIZE/Byte.SIZE) * 3  ) {
				while (runs.size() < nblocks * run_control){
					word_id = input.readInt();
					doc_id = input.readInt();
					pos = input.readInt();
					runs.add(MutableTriple.of(word_id,doc_id,pos));
				}
			} else {
				while (input.available() != 0){
					word_id = input.readInt();
					doc_id = input.readInt();
					pos = input.readInt();
					runs.add(MutableTriple.of(word_id,doc_id,pos));
				}
=======
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile),blocksize));
		DataOutputStream run_writer;
		ArrayList<Triple<Integer, Integer, Integer>> runs = new ArrayList<Triple<Integer, Integer, Integer>>();
		int records = blocksize / ((Integer.SIZE/Byte.SIZE) * 3); // 한 블럭당 튜플 개수
		int word_id, doc_id, pos;
		int run = records * 3 * (Integer.SIZE/Byte.SIZE) * nblocks; // 한 run의 용량
		int run_cnt = 1;
		int pass_cnt = 1;
		while(input.available() >= run){
			for(int i=0; i<records * nblocks; i++){
				word_id = input.readInt();
				doc_id = input.readInt();
				pos = input.readInt();
				runs.add(Triple.of(word_id,doc_id,pos));
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
			}
			Collections.sort(runs, new TripleSort());
			run_writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/run_"+pass_cnt+"_"+run_cnt+".data"),blocksize));
			for(Triple<Integer,Integer,Integer> tuple : runs){
				run_writer.writeInt(tuple.getLeft());
				run_writer.writeInt(tuple.getMiddle());
				run_writer.writeInt(tuple.getRight());
			}
			run_writer.close();
<<<<<<< HEAD
			run_cnt++;
			runs.clear();
		}
		run_cnt--;
		input.close();
		// create run 완료
		// merge pass 시작
		PriorityQueue<Tuple> tuples = new PriorityQueue<Tuple>();
		ArrayList<DataInputStream> run_reads = new ArrayList<DataInputStream>();
		DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile),blocksize));
		
=======
			System.out.println((run_cnt++)+" runs");
			runs.clear();
		}
		int remains = input.available() / ((Integer.SIZE/Byte.SIZE) * 3);
		for(int i=0; i<remains; i++){
			word_id = input.readInt();
			doc_id = input.readInt();
			pos = input.readInt();
			runs.add(Triple.of(word_id,doc_id,pos));
		}
		Collections.sort(runs, new TripleSort());
		run_writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/run_"+pass_cnt+"_"+run_cnt+".data"),blocksize));
		for(Triple<Integer,Integer,Integer> tuple : runs){
			run_writer.writeInt(tuple.getLeft());
			run_writer.writeInt(tuple.getMiddle());
			run_writer.writeInt(tuple.getRight());
		}
		run_writer.close();
		System.out.println((run_cnt)+" runs");
		System.out.println("create runs");
		input.close();
		// create run 완료
		// merge pass 시작
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		ArrayList<DataInputStream> run_reads = new ArrayList<DataInputStream>();
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
		Tuple tuple;
		int index;
		while(true){
			int pre_runs = run_cnt;
			int cur_runs = pre_runs/nblocks;
			if(cur_runs*nblocks < pre_runs) cur_runs++;
			pass_cnt++;
			run_cnt = 1;
<<<<<<< HEAD
			int iter;
			if(pre_runs > nblocks){
				iter = nblocks;
				pre_runs -= nblocks;
			}
			else iter = pre_runs;
			
			for(int i=1; i<=cur_runs; i++){
				for(int j=0; j<iter; j++){
					DataInputStream run_read = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/run_"+(pass_cnt-1)+"_"+(j+1)+".data"),blocksize));
					run_reads.add(run_read);
					word_id = run_reads.get(j).readInt();
					doc_id = run_reads.get(j).readInt();
					pos = run_reads.get(j).readInt();
					tuples.offer(new Tuple(j,word_id,doc_id,pos));
				}
				if(cur_runs == 1) run_writer = output;
				else run_writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/run_"+pass_cnt+"_"+run_cnt+".data"),blocksize));
				while(true){
					tuple = tuples.poll();
=======
			int run_num = 1;
			int iter;
			for(int i=1; i<=cur_runs; i++){
				run_reads.clear();
				tuples.clear();
				if(pre_runs > nblocks){
					iter = nblocks;
					pre_runs -= nblocks;
				}
				else iter = pre_runs;
				for(int j=0; j<iter; j++){
					DataInputStream run_read = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/run_"+(pass_cnt-1)+"_"+run_num+".data"),blocksize));
					run_reads.add(run_read);
					run_num++;
				}
				if(cur_runs == 1) run_writer = output;
				else run_writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/run_"+pass_cnt+"_"+run_cnt+".data"),blocksize));
				for(int j=0; j<iter; j++){
					word_id = run_reads.get(j).readInt();
					doc_id = run_reads.get(j).readInt();
					pos = run_reads.get(j).readInt();
					tuple = new Tuple(j,word_id,doc_id,pos);
					tuples.add(tuple);
				}
				while(true){
					Collections.sort(tuples, new TupleSort());
					tuple = tuples.get(0);
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
					index = tuple.index;
					run_writer.writeInt(tuple.word_id);
					run_writer.writeInt(tuple.doc_id);
					run_writer.writeInt(tuple.pos);
<<<<<<< HEAD
=======
					tuples.remove(0);
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
					if(run_reads.get(index).available() > 0){
						word_id = run_reads.get(index).readInt();
						doc_id = run_reads.get(index).readInt();
						pos = run_reads.get(index).readInt();
						tuple = new Tuple(index,word_id,doc_id,pos);
<<<<<<< HEAD
						tuples.offer(tuple);
					}
					if(tuples.isEmpty()) break;
				}
				run_cnt++;
				run_writer.close();
				run_reads.clear();
				tuples.clear();
=======
						tuples.add(tuple);
					}
					if(tuples.isEmpty()) break;
				}
				System.out.println((run_cnt++)+" runs");
				run_writer.close();
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
			}
			if(run_cnt == 2){
				break;
			}
			else{
<<<<<<< HEAD
=======
				System.out.println((pass_cnt-1)+" merge");
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
				run_cnt--;
			}
		}
//		merge pass 완료
		output.close();
<<<<<<< HEAD
	}
}
=======
		System.out.println("Finished");
	}
}
>>>>>>> 2a5acbaedb6025bf4dbe287684da40223f5680f9
