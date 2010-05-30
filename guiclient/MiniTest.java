import java.util.LinkedList;

public class MiniTest {
	public static void main(String args[]) {
		final LinkedList<String> l = new LinkedList<String>();
		l.add("I am not sure");
		l.add("C++ would let me");
		l.add("do this to a const.");
		for(String a : l) {
			System.out.println(a);
		}
	}
}
