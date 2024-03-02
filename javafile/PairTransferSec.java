package routesearch.data.javafile;

public class PairTransferSec {
    private final String string1;
    private final String string2;
    private int transferSec;
    
    PairTransferSec(String string1, String string2, int transferSec){
    	this.string1 = string1;
    	this.string2 = string2;
    	this.transferSec = transferSec;
    }
    
    
    //string1とstring2のみを比較してpairと指定したオブジェクトが同じ文字列ならtrue
    public boolean pairEquals(PairTransferSec pair) {
    	return containsPair(pair.get1(),pair.get2());
    }
    
   
    //string1とstring2を両方含むか
    public boolean containsPair(String string1, String string2) {
    	if(string1.equals(string2) || string1==null || string2==null) {
    		return false;
    	}else {
    		return containsOne(string1) && containsOne(string2);
    	}
    }
    
    //this.line1とthis.line2の一方がlineかどうか
    private boolean containsOne(String string) {
    	return string.equals(string1) || string.equals(string2); 
    }
    
    //getter
    public String getOther(String string) {
    	if(string1.equals(string)) {
    		return string2;
    	}else if(string2.equals(string)) {
    		return string1;
    	}
    	return null;
    }
    
    public String get1() {
    	return string1;
    }
    
    public String get2() {
    	return string2;
    }
    
    public int getValue() {
    	return transferSec;
    }
    
    //setter
    public void setValue(int newTransferSec) {
    	transferSec = newTransferSec;
    }
}