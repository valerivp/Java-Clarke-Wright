import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

class Route implements Comparable<Route>
{
	private int _capacity;
	private int _weight;
	private double _cost;
	private double _savings;
	public ArrayList<Customer> customers;

	private void calculateSavings(){
		double originalCost = 0;
		double newCost = 0;
		double tempcost =0;
		Customer prev = null;
		
		//Foreach customer in the route:
		for(Customer c:customers){ 
			// Distance from Depot
			tempcost = Math.sqrt((c.x*c.x)+(c.y*c.y));
			originalCost += (2.0*tempcost);

			if(prev != null){
				// Distance from previous customer to this customer
				double x = (prev.x - c.x);
				double y = (prev.y - c.y);
				newCost += Math.sqrt((x*x)+(y*y));
			}else{
				//If this is the first customer in the route, no change
				newCost += tempcost;
			}
			prev = c;
		}
		newCost += tempcost;
		_cost = newCost;
		_savings = originalCost - newCost;
	}

	public Route(int capacity){
		_capacity = capacity;
		customers = new ArrayList<Customer>();
		_weight =0;
		_cost= 0;
		_savings =0;
	}

	public void addCustomer(Customer c, boolean order){
		//Add customer to the start or end of the route?
		if(order){
			customers.add(0,c);
		}else{
			customers.add(c);
		}

		if(c.c > _capacity){
			System.out.println("Customer order too large");
		}
		
		_weight += c.c;

		if(_weight > _capacity){
			System.out.println("Route Overloaded");
		}

		calculateSavings();
	}

	public double getSavings(){
		return _savings;
	}
	public double getCost(){
		return _cost;
	}
	public int getWeight(){
		return _weight;
	}
	public int compareTo(Route r) {
		return Double.compare(r.getSavings(), this._savings);
	}

}

public class ClarkeWright 
{
	public static int truckCapacity = 0;

	public static ArrayList<List<Customer>> solve(ArrayList<Customer> customers){
		ArrayList<List<Customer>> solution = new ArrayList<List<Customer>>();

		HashSet<Customer> abandoned = new HashSet<Customer>();

		//calculate the savings of all the pairs
		ArrayList<Route> pairs = new ArrayList<Route>(); 

		for(int i=0; i<customers.size(); i++){
			for(int j=i+1; j<customers.size(); j++){
				Route r = new Route(truckCapacity);
				r.addCustomer(customers.get(i),false);
				r.addCustomer(customers.get(j),false);
				pairs.add(r);
			}
		}
		//order pairs by savings
		Collections.sort(pairs);

		//start combining pairs into routes
		for(int i=0; i<pairs.size(); i++)
		{
			Route ro = pairs.get(i);

			for(int j=i+1; j<pairs.size(); j++){
				Route r = pairs.get(j);
				Customer c1 = r.customers.get(0);
				Customer c2 = r.customers.get(r.customers.size()-1);
				Customer cr1 = ro.customers.get(0);
				Customer cr2 = ro.customers.get(ro.customers.size()-1);

				//do they have any common nodes?
				if(c1 == cr1){
					//could we combine these based on weight?
					if(c2.c + ro.getWeight() <= truckCapacity){
						//Does the route already contain BOTH these nodes already?
						if(!ro.customers.contains(c2)){
							ro.addCustomer(c2, true);
						}
					}
				}else if (c1 == cr2){
					if(c2.c + ro.getWeight() <= truckCapacity){
						if(!ro.customers.contains(c2)){
							ro.addCustomer(c2, false);
						}
					}
				}else if (c2 == cr1){
					if(c1.c + ro.getWeight() <= truckCapacity){
						if(!ro.customers.contains(c1)){
							ro.addCustomer(c1, true);
						}
					}
				}else if (c2 ==cr2){
					if(c1.c + ro.getWeight() <= truckCapacity){
						if(!ro.customers.contains(c1)){
							ro.addCustomer(c1, false);
						}
					}
				}
			}

			//Remove any pairs that have visited customers
			// Also keep a tab on any customers we remove
			for(int j=i+1; j<pairs.size(); j++){				
				Route r = pairs.get(j);
				Customer c1 = r.customers.get(0);
				Customer c2 = r.customers.get(1);
				byte a = 0;
				if(ro.customers.contains(c1)){
					a+=1;
				}
				if(ro.customers.contains(c2)){
					a+=2;
				}
				if(a>0){
					if(a == 1){
						abandoned.add(c2);
					}else if(a == 2){
						abandoned.add(c1);
					}else if(a == 3){
						abandoned.remove(c1);
						abandoned.remove(c2);
					}
					pairs.remove(r);
					j--;
				}

			}

		}

		//Edge case: A single Customer can be left out of all routes due to capacity constraints
		// abandoned keeps track of all customers not attached to a route
		for(Customer C:abandoned){
			//we could tack this onto the end of a route if it would fit
			//or just create a new route just for it. As per the Algorithm 
			ArrayList<Customer> l = new ArrayList<Customer>();
			l.add(C);
			solution.add(l);
		}

		//output
		for(Route r:pairs){
			ArrayList<Customer> l = new ArrayList<Customer>();
			l.addAll(r.customers);
			solution.add(l);
		}
		return solution;
	}
	
	public static ArrayList<List<Customer>> solveP(ArrayList<Customer> customers){
		ArrayList<List<Customer>> solution = new ArrayList<List<Customer>>();
		System.out.println("PRRAARARALELEL");
		HashSet<Customer> abandoned = new HashSet<Customer>();

		//calculate the savings of all the pairs
		ArrayList<Route> pairs = new ArrayList<Route>(); 

		for(int i=0; i<customers.size(); i++){
			for(int j=i+1; j<customers.size(); j++){
				Route r = new Route(truckCapacity);
				r.addCustomer(customers.get(i),false);
				r.addCustomer(customers.get(j),false);
				pairs.add(r);
			}
		}
		//order pairs by savings
		Collections.sort(pairs);
		for(Route ro :pairs)
		{
			System.out.println(System.identityHashCode(ro.customers.get(0))+" - "+ro.customers.get(0).c+"  :  "+ System.identityHashCode(ro.customers.get(1))+" - "+ro.customers.get(1).c);
		}
		
		
		HashSet<Route> routes = new HashSet<Route>();
		routes.add(pairs.get(0));
		Remove(pairs.get(0).customers.get(0),pairs.get(0).customers.get(1),pairs);
		
		//start combining pairs into routes
		outerloop: for(int j=0; j<pairs.size(); j++){
			Route r = pairs.get(j);
			Customer c1 = r.customers.get(0);
			Customer c2 = r.customers.get(r.customers.size()-1);
			
			for(Route ro :routes)
			{
				Customer cr1 = ro.customers.get(0);
				Customer cr2 = ro.customers.get(ro.customers.size()-1);
				

				//do they have any common nodes?
				if(c1 == cr1){
					//could we combine these based on weight?
					if(c2.c + ro.getWeight() <= truckCapacity){
						//Does the route already contain BOTH these nodes already?
						if(!ro.customers.contains(c2)){
							ro.addCustomer(c2, true);
							abandoned.remove(c2);
							Remove(c1,c2,pairs);
							j=0;
							continue outerloop;
						}
					}
				}else if (c1 == cr2){
					if(c2.c + ro.getWeight() <= truckCapacity){
						if(!ro.customers.contains(c2)){
							ro.addCustomer(c2, false);
							abandoned.remove(c2);
							Remove(c1,c2,pairs);
							j=0;
							continue outerloop;
						}
					}
				}
				if (c2 == cr1){
					if(c1.c + ro.getWeight() <= truckCapacity){
						if(!ro.customers.contains(c1)){
							ro.addCustomer(c1, true);
							abandoned.remove(c1);
							Remove(c1,c2,pairs);
							j=0;
							continue outerloop;
						}
					}
				}else if (c2 ==cr2){
					if(c1.c + ro.getWeight() <= truckCapacity){
						if(!ro.customers.contains(c1)){
							ro.addCustomer(c1, false);
							abandoned.remove(c1);
							Remove(c1,c2,pairs);
							j=0;
							continue outerloop;
						}
					}	
				}
			}
			
			//If we reach here then the pair hasn't been added to any routes
			
			boolean c = false;
			ArrayList<Customer> notVisited = new ArrayList<Customer>();
			
			for(Route ro :routes)
			{
				for(Customer cc:r.customers){
					if(ro.customers.contains(cc)){
						c = true;
					}else{
						notVisited.add(cc);
					}
				}
			}
			
			if(!c){
				//none of the customers on it are in a previous route
				//so we get to make a new one!
				System.out.println("New route!");
				abandoned.remove(c1);
				abandoned.remove(c2);
				routes.add(r);
				Remove(c1,c2,pairs);
			}else{
				//one of it's customers is already in a route
				//so we put the unvisited customer in the safe zone and remove the route.
				abandoned.addAll(notVisited);
				pairs.remove(r);
			}
			
			j=0;
			
		}
		
		

		//Edge case: A single Customer can be left out of all routes due to capacity constraints
		// abandoned keeps track of all customers not attached to a route
		for(Customer C:abandoned){
			//we could tack this onto the end of a route if it would fit
			//or just create a new route just for it. As per the Algorithm 
			ArrayList<Customer> l = new ArrayList<Customer>();
			l.add(C);
		//	solution.add(l);
		}

		//output
		for(Route r:routes){
			ArrayList<Customer> l = new ArrayList<Customer>();
			l.addAll(r.customers);
			solution.add(l);
		}
		return solution;
	}
	
	private static void Remove(Customer c1, Customer c2, ArrayList<Route> routes)
	{
		 for(int i=0; i<routes.size(); i++){
			Route r = routes.get(i);
			if(c1 != null){
				if(r.customers.contains(c1)){
					routes.remove(i);
					i--;
					continue;
				}
			}
			if(c2 != null){
				if(r.customers.contains(c1)){
					routes.remove(i);
					i--;
					continue;
				}
			}
		}
	}
	
}


















