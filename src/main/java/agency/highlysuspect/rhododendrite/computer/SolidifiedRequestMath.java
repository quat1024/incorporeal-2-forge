package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;

//In here, not in Incorporeal, since compound requests are a concept of Rhododendrite only
public class SolidifiedRequestMath {
	public static SolidifiedRequest add(SolidifiedRequest a, SolidifiedRequest b) {
		return new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.union(a.matcher, b.matcher),
			a.count + b.count
		);
	}
}
