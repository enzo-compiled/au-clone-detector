package cl.uoh.abaumgart.eqnauac.data.term;

public interface Symbol {
	static final Symbol DUMMY = new Symbol() {
		@Override
		public String toString() {
			return ".";
		}
	};
}
