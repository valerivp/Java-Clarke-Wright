import java.util.*;
public class Experiment {

	public static void main(String[] args)throws Exception{
		String outdir = "output/";
		String problemdir = "tests/";
		String [] probs = {
				"rand00100",
				"rand00150",
				"rand00200",
				"rand00250",
				"rand00300",
				"rand00350",
				"rand00400",
				"rand00450",
				"rand00500",
				"rand00550",
				"rand00600",
				"rand00650",
				"rand00700",
				"rand00750",
				"rand00800",
				"rand00850",
				"rand00900",
				"rand00950",
				"rand01000"
				};
		for (String f:probs){
			ArrayList<Long> timing = new ArrayList<Long>();
			VRProblem vrp = new VRProblem(problemdir+f+"prob.csv");
			VRSolution vrs = new VRSolution(vrp);
			System.out.printf("%s, %d\n",f,vrp.size());
			for(int i=0;i<50;i++){
				long start = System.nanoTime();
				vrs.clarkeWrightSolution(false);
				long delta = System.nanoTime()-start;
				timing.add(delta);
				System.out.print(delta+", ");
			}
			System.out.print("\n\n");
			vrs.writeOut(outdir+f+"CWsn.csv");
		}
		
	}
}
