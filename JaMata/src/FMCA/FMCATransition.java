package FMCA;


import java.util.Arrays;







import CA.CA;
import CA.CAState;
import CA.CATransition;



/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FMCATransition extends CATransition implements java.io.Serializable{ 
	//private boolean must;  
	public enum action{
		PERMITTED,URGENT,GREEDY,LAZY
	}
	private action type;
	/**
	 * 
	 * @param initial		source state
	 * @param label2			label
	 * @param fina			arrival state
	 */
	public FMCATransition(CAState initial, String[] label2, CAState fina, action type)
	{
		super(initial,label2,fina);
		this.type=type;
	}
	
	
	public boolean isUrgent()
	{
		return (this.type==action.URGENT);
	}
	
	public boolean isGreedy()
	{
		return (this.type==action.GREEDY);
	}
	
	
	public boolean isLazy()
	{
		return (this.type==action.LAZY);
	}
	
	
	public boolean isMust()
	{
		return (this.type!=action.PERMITTED);
	}
	
	public action getType()
	{
		return this.type;
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		switch (this.type) 
		{
			case PERMITTED: return "("+Arrays.toString(getSourceP().getState())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP().getState())+")";
			case URGENT:return "!U("+Arrays.toString(getSourceP().getState())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP().getState())+")";
			case GREEDY:return "!G("+Arrays.toString(getSourceP().getState())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP().getState())+")";
			case LAZY:return "!L("+Arrays.toString(getSourceP().getState())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP().getState())+")";		
		}
		return null;
	}

	//TODO equals should check the other fields of CAState
	public boolean equals(FMCATransition t)
	{
		FMCATransition tr=(FMCATransition) t;
		int[] ip =tr.getSourceP().getState();
		String[] lp=tr.getLabelP();
		int[] dp=tr.getTargetP().getState();
		action type=tr.getType();
		return ( Arrays.equals(ip,getSourceP().getState()))&&(Arrays.equals(lp,getLabelP()))&&(Arrays.equals(dp,this.getTargetP().getState())&&(this.type==type));
	}	
	
	/**
	 * aka  controllable greedy/lazy request
	 * @return	true if the  greedy/lazy transition request is matched 
	 */
	protected  boolean isMatched(FMCA aut)
	{
		FMCATransition[] tr = aut.getTransition();
//		int[][] fs=aut.allFinalStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.isRequest())
			&&(this.isGreedy()||this.isLazy()))
		{
			CAState[] R=aut.getDanglingStates();
//			if (!MSCAUtil.contains(this.getSource(), fs)) // if source state is not final
//				return true;
			for (int j=0;j<tr.length;j++)	
			{
				if ((tr[j].isMatch())
					&&((tr[j].isGreedy()&&this.isGreedy())||(tr[j].isLazy()&&this.isLazy()))//the same type (greedy or lazy)
					&&(tr[j].getReceiver()==this.getReceiver())	//the same principal
					&&(tr[j].getSourceP().getState()[tr[j].getReceiver()]==this.getSourceP().getState()[this.getReceiver()]) //the same source state					
					&&(tr[j].getLabelP()[tr[j].getReceiver()].equals(this.getLabelP()[this.getReceiver()])) //the same request
					&&(!FMCAUtil.contains(this.getSourceP(), R))) //source state is not redundant
					{
						return true;
					}
			}
			return false;
		}
		return true; // trivially matched, it is not a request or it is not greedy or lazy
	}



	/**
	 * 
	 * @return a new request transition where the sender of the match is idle
	 */
	public FMCATransition extractRequestFromMatch()
	{
		if (!this.isMatch())
			return null;
		//int length=this.getSourceP().length;
		int sender=this.getSender();
		CAState source= this.getSourceP().clone();
		CAState target= this.getTargetP().clone();
		String[] request=Arrays.copyOf(this.getLabelP(), this.getLabelP().length);
		target.getState()[sender]=source.getState()[sender];  //the sender is now idle
		request[sender]=CATransition.idle;  //swapping offer to idle
		return new FMCATransition(source,request,target,this.type); //returning the request transition
		
	}
	
	/**
	 * aka uncontrollable lazy match
	 * @return	true if the  lazy match transition is lazy unmatchable in aut
	 */
	protected  boolean isLazyUnmatchable(FMCA aut)
	{
		FMCATransition[] tr = aut.getTransition();
//		int[][] fs=aut.allFinalStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.isMatch())
			&&(this.isLazy()))
		{
			for (int j=0;j<tr.length;j++)	
			{
				if (this.equals(tr[j]))
					return false; //the transition must not be in aut
			}
			FMCATransition t= this.extractRequestFromMatch(); //extract the request transition from this
			return !t.isMatched(aut); 
		}
		else
			return false;
	}
	
	/**
	 * 
	 * @param aut
	 * @return	true if the transition is uncontrollable in aut
	 */
	protected boolean isUncontrollable(FMCA aut)
	{
		return this.isUrgent()||(this.isMatch()&&this.isGreedy())||!this.isMatched(aut)||this.isLazyUnmatchable(aut);
		
	}
	
	protected boolean isForbidden(Product p)
	{
		return (FMCAUtil.getIndex(p.getForbidden(),this.getUnsignedAction())>=0);
	}
	
	protected boolean isRequired(Product p)
	{
		return (FMCAUtil.getIndex(p.getRequired(),this.getUnsignedAction())>=0);		
	}
	
	/**
	 *
	 * @param t
	 * @return   source states of transitions in t 
	 */
	protected static CAState[] getSources(FMCATransition[] t)
	{
		CAState[] s= new CAState[t.length];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if (!FMCAUtil.contains(t[i].getSourceP(), s)) //if the source state was not already inserted previously
			{
				s[pointer]=t[i].getSourceP();
				pointer++;
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer);
		return s;
	}

	/**
	 * @param t
	 * @param aut
	 * @return   source states of transitions in t that are unmatched or lazy unmatchable in aut
	 */
	protected static CAState[] areUnmatchedOrLazyUnmatchable(FMCATransition[] t, FMCA aut)
	{
		CAState[] s= new CAState[t.length];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if ((!t[i].isMatched(aut))||(t[i].isLazyUnmatchable(aut)))
			{
				if (!FMCAUtil.contains(t[i].getSourceP(), s)) //if the source state was not already inserted previously
				{
					s[pointer]=t[i].getSourceP();
					pointer++;
				}
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer);
		return s;
	}
	
	/**
	 * This method is different from the corresponding one in CATransition class because it deals with modal actions. 
	 * Moreover insert is changed.
	 * 
	 * 
	 * @param t				first transition to move
	 * @param tt			second transition to move only in case of match
	 * @param firstprinci  the index to start to copy the principals in t
	 * @param firstprincii the index to start to copy the principals in tt
	 * @param insert		the states of all other principals who stays idle
	 * @param aut		array of principals automata, used here to retrieve the states of idle principals using insert as pointer  
	 * 					 the field CAState[] states must be instantiated --> not modified
	 * @return				a new transition where only principals in t (and tt) moves while the other stays idle in their state given in insert[]
	 */
	public FMCATransition generateATransition(CATransition t, CATransition tt, int firstprinci, int firstprincii,int[] insert,CA[] aut)
	{
		//TODO I should replace the composition of CA with a forward visit of the automaton
		
		if (tt!=null) //if it is a match
		{
			int[] s=((FMCATransition) t).getSourceP().getState();
			String[] l=((FMCATransition) t).getLabelP();
			int[] d=((FMCATransition) t).getTargetP().getState();
			int[] ss = ((FMCATransition) tt).getSourceP().getState();
			String[] ll=((FMCATransition) tt).getLabelP();
			int[] dd =((FMCATransition) tt).getTargetP().getState();
			int[] source = new int[insert.length+s.length+ss.length];
			int[] target = new int[insert.length+s.length+ss.length];
			String[] label = new String[insert.length+s.length+ss.length];
			action type;
			if (((FMCATransition) t).isRequest())
				type=((FMCATransition) t).getType();
			else
				type=((FMCATransition) tt).getType();
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						source[i+j]=s[j];
						label[i+j]=l[j];
						if (l[j]==null)
						{
							System.out.println("vai");
						}
						target[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else 
				{
					if (i==firstprincii)
					{
						for (int j=0;j<ss.length;j++)
						{
							source[i+counter+j]=ss[j];
							label[i+counter+j]=ll[j];
							if (ll[j]==null)
							{
								System.out.println("vai");
							}
							target[i+counter+j]=dd[j];
						}
						counter+=ss.length;//record the shift due to the second CA 
						i--;
						firstprincii=-1;
					}	
					else 
					{
						source[i+counter]=  ((FMCA)aut[i+counter]).getState()[insert[i]].getState()[0];//insert[i]; //TODO modify here! this should have been fixed
						target[i+counter]=  ((FMCA)aut[i+counter]).getState()[insert[i]].getState()[0];//insert[i];	//TODO modify here! this should have been fixed
						label[i+counter]=CATransition.idle;
					}
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA was the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					source[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					if (l[j]==null)
					{
						System.out.println("vai");
					}
					target[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			if (firstprincii==insert.length) //case limit, the second CA was the last of aut
			{
				for (int j=0;j<ss.length;j++)
				{
					source[insert.length+counter+j]=ss[j];
					label[insert.length+counter+j]=ll[j];
					if (ll[j]==null)
					{
						System.out.println("vai");
					}
					target[insert.length+counter+j]=dd[j];
				}
			}
			return new FMCATransition(new CAState(source),label,new CAState(target),type);	
		}
		else	//is not a match
		{
			int[] s=((FMCATransition) t).getSourceP().getState();
			String[] l=((FMCATransition) t).getLabelP();
			int[] d=((FMCATransition) t).getTargetP().getState();
			int[] source = new int[insert.length+s.length];
			int[] target = new int[insert.length+s.length];
			String[] label = new String[insert.length+s.length];
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						source[i+j]=s[j];
						label[i+j]=l[j];
						target[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else
				{
					try{
					source[i+counter]=((FMCA)aut[i+counter]).getState()[insert[i]].getState()[0]; //insert[i];//TODO modify here! this should have been fixed
					target[i+counter]=((FMCA)aut[i+counter]).getState()[insert[i]].getState()[0]; //insert[i];//TODO modify here! this should have been fixed
					label[i+counter]=CATransition.idle;
					} catch (NullPointerException e) 
					{
						System.out.println("prova");
					}
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA is the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					source[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					target[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			return new FMCATransition(new CAState(source),label,new CAState(target),((FMCATransition) t).getType());	
		}
	}
	
	/**
	 * Returns all the transitions starting from a state source
	 * @param source		the initial state
	 * @param tr	the array of transitions
	 * @return the transitions of tr starting from state spirce
	 */
	public static FMCATransition[] getTransitionFrom(CAState source, FMCATransition[] tr)
	{
		if (tr==null)
			return null;
		FMCATransition[] newtr = new FMCATransition[tr.length];
		int j=0;
		for (int i=0;i<tr.length;i++)
		{
			if (tr[i]!=null&&(Arrays.equals(source.getState(), tr[i].getSourceP().getState())))
			{
				newtr[j]=tr[i];
				j++;
			}
		}
		if (j==0)
			return new FMCATransition[0];
		newtr = FMCAUtil.removeTailsNull(newtr, j);
		return newtr;
	}
	
	public static FMCATransition[] getTransitionTo(CAState target, FMCATransition[] tr)
	{
		if (tr==null)
			return null;
		FMCATransition[] newtr = new FMCATransition[tr.length];
		int j=0;
		for (int i=0;i<tr.length;i++)
		{
			if (Arrays.equals(target.getState(), tr[i].getTargetP().getState()))
			{
				newtr[j]=tr[i];
				j++;
			}
		}
		if (j==0)
			return new FMCATransition[0];
		newtr = FMCAUtil.removeTailsNull(newtr, j);
		return newtr;
	}
}