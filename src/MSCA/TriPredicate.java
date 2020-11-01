package MSCA;

/**
 * used in the synthesis method of MSCA for readability. 
 * @author Davide
 *
 * @param <T>
 * @param <U>
 * @param <V>
 */
public interface TriPredicate<T,U,V> {
	public boolean test(T arg1, U arg2, V arg3);
}

