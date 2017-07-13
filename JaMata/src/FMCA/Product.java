package FMCA;

import java.util.Arrays;

import CA.CATransition;

public class Product {
	private String[] required;
	private String[] forbidden;
	
	public Product(String[] r, String[] f)
	{
		//all positive integers, to avoid sign mismatches
		String[] rp = new String[r.length];
		for (int i=0;i<r.length;i++)
			rp[i]=CATransition.getUnsignedAction(r[i]);

		String[] fp = new String[f.length];
		for (int i=0;i<f.length;i++)
			fp[i]=CATransition.getUnsignedAction(f[i]);

		this.required=rp;
		this.forbidden=fp;
	}

	public String[] getRequired()
	{
		return required;
	}
	
	public String[] getForbidden()
	{
		return forbidden;
	}
	
	public int getForbiddenAndRequiredNumber()
	{
		return required.length+forbidden.length;
	}
	
	/**
	 * check if all features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsFeatures(Product p)
	{
		String[] rp=p.getRequired();
		String[] rf=p.getForbidden();
		for(int i=0;i<rp.length;i++)
			if (!FMCAUtil.contains(rp[i], this.required))
				return false;
		for(int i=0;i<rf.length;i++)
			if (!FMCAUtil.contains(rf[i], this.forbidden))
				return false;
		
		return true;
	}
	
	/**
	 * check if all forbidden features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsForbiddenFeatures(Product p)
	{
		String[] rf=p.getForbidden();
		for(int i=0;i<rf.length;i++)
			if (!FMCAUtil.contains(rf[i], this.forbidden))
				return false;
		
		return true;
	}
	
	/**
	 * check if all required features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsRequiredFeatures(Product p)
	{
		String[] rf=p.getRequired();
		for(int i=0;i<rf.length;i++)
			if (!FMCAUtil.contains(rf[i], this.required))
				return false;
		
		return true;
	}
	
	/**
	 * 
	 * @param f
	 * @return  true if feature f is contained (either required or forbidden)
	 */
	public boolean containFeature(String f)
	{
		String[] s= new String[1];
		s[0]=f;
		Product temp = new Product(s,s);
		return (this.containsRequiredFeatures(temp)||this.containsForbiddenFeatures(temp));
	}
	
	
	/**
	 * 
	 * @param t
	 * @return true if all required actions are available in the transitions t
	 */
	public boolean checkRequired(FMCATransition[] t)
	{
		
		for (int i=0;i<this.required.length;i++)
		{
			boolean found=false;
			for (int j=0;j<t.length;j++)
			{
				if (CATransition.getUnsignedAction(t[j].getAction()).equals(this.required[i]))  //do not differ between requests and offers
					found=true;
			}
			if (!found)
				return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param t
	 * @return true if all forbidden actions are not available in the transitions t
	 */
	public boolean checkForbidden(FMCATransition[] t)
	{
		
		for (int i=0;i<this.forbidden.length;i++)
		{
			for (int j=0;j<t.length;j++)
			{
				if (CATransition.getUnsignedAction(t[j].getAction()).equals(this.forbidden[i]))  //do not differ between requests and offers
					return false;
			}
		}
		return true;
	}
	
	
	public boolean isValid(FMCA aut)
	{
		FMCATransition[] t=aut.getTransition();
		return this.checkForbidden(t)&&this.checkForbidden(t);
	}
	public String toString()
	{
		return "R:"+Arrays.toString(required)+";\nF:"+Arrays.toString(forbidden)+";\n";
	}
	
	public String toHTMLString(String s)
	{
        return "<html>"+s+"R:"+Arrays.toString(required)+"<br />F:"+Arrays.toString(forbidden)+"</html>";
	
	}
	
	/**
	 * 
	 * @param p
	 * @return true if both products have the same required and forbidden features
	 */
	public boolean equals(Product p)
	{
		return (
			((p.getRequired().length==required.length)&&(this.containsRequiredFeatures(p)))
			&&
			((p.getForbidden().length==forbidden.length)&&(this.containsForbiddenFeatures(p)))			
			);
	}
}
