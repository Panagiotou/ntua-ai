
import java.io.IOException;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;

public class PrologExample {

	public static void main(String[] args) throws JIPSyntaxErrorException, IOException {
		
		JIPEngine jip = new JIPEngine();
		jip.consultFile("nodes.pl");
		
		JIPTermParser parser = jip.getTermParser();
	
		
		JIPQuery jipQuery; 
		JIPTerm term;
		
		
		System.out.println("CASE 2");
		jipQuery = jip.openSynchronousQuery(parser.parseTerm("nodes(X,Y,Z,W,R,A)."));
		term = jipQuery.nextSolution();
		while (term != null) {
			System.out.println(term.getVariablesTable().get("Y").toString());
			term = jipQuery.nextSolution();
		}


	}
}
