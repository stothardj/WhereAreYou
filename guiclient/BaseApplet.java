import javax.swing.JApplet;

public abstract class BaseApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public abstract void createGUI();
}
