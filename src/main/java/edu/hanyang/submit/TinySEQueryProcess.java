package edu.hanyang.submit;

import java.io.IOException;

import edu.hanyang.indexer.DocumentCursor;
import edu.hanyang.indexer.PositionCursor;
import edu.hanyang.indexer.IntermediateList;
import edu.hanyang.indexer.IntermediatePositionalList;
import edu.hanyang.indexer.QueryPlanTree;
import edu.hanyang.indexer.QueryProcess;
import edu.hanyang.indexer.StatAPI;

public class TinySEQueryProcess implements QueryProcess {

   @Override
   public void op_and_w_pos(DocumentCursor op1, DocumentCursor op2, int shift, IntermediatePositionalList out)
         throws IOException {
      while (!op1.is_eol() && !op2.is_eol()) {
         if (op1.get_docid() < op2.get_docid()) {
            op1.go_next();
         }
         else if (op1.get_docid() > op2.get_docid()) {
            op2.go_next();
         }
         else {
            PositionCursor q1, q2;
            q1 = op1.get_position_cursor();
            q2 = op2.get_position_cursor();
            while (!q1.is_eol() && !q2.is_eol()) {
               if (q1.get_pos() + shift < q2.get_pos()) {
                  q1.go_next();
               }
               else if (q1.get_pos() + shift > q2.get_pos()) {
                  q2.go_next();
               }
               else {
                  out.put_docid_and_pos(op1.get_docid(), q1.get_pos());
                  q1.go_next();
                  q2.go_next();
               }
            }
            op1.go_next();
            op2.go_next();
         }
      }
      
   }
   
   @Override
   public void op_and_wo_pos(DocumentCursor op1, DocumentCursor op2, IntermediateList out) throws IOException {
      while (!op1.is_eol() && !op2.is_eol()) {
         if (op1.get_docid() < op2.get_docid()) {
            op1.go_next();
         }
         else if (op1.get_docid() > op2.get_docid()) {
            op2.go_next();
         }
         else {
            out.put_docid(op1.get_docid());
            op1.go_next();
            op2.go_next();
         }
      }
      
   }

   @Override
   public QueryPlanTree parse_query(String query, StatAPI stat) throws Exception {
      // TODO Auto-generated method stub
      return null;
   }


}