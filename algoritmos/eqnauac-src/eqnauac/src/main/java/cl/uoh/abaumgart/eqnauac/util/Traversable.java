package cl.uoh.abaumgart.eqnauac.util;

import cl.uoh.abaumgart.eqnauac.data.term.NominalTerm;

public interface Traversable<T> {
	public boolean traverse(TraverseCallBack<T> callBack);

	/**
	 * This class may be used to
	 * {@linkplain NominalTerm#traverse(TraverseCallBack) traverse} a term tree
	 * and execute an arbitrary operation on every node.The following example
	 * prints all variable occurences inside of a nominal term.
	 * 
	 * <pre>
	 * term.traverse(new TraverseCallBack&lt;NominalTerm&gt;() {
	 * 	public boolean exec(NominalTerm term) {
	 * 		if (term instanceof Suspension)
	 * 			System.out.println(((Suspension) term).getVar());
	 * 		return false;
	 * 	}
	 * });
	 * </pre>
	 * 
	 * @author Alexander Baumgartner
	 */
	public static abstract class TraverseCallBack<T> {
		/**
		 * Forward propagation will stop as soon as this method returns true.
		 */
		public abstract boolean exec(T term);

		/**
		 * Traversing will stop as soon as this method returns true.
		 */
		public boolean execBackward(T term) {
			return false;
		}
	}

}
