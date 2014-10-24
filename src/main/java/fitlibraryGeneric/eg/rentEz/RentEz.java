package fitlibraryGeneric.eg.rentEz;

import fitlibrary.traverse.DomainAdapter;

public class RentEz implements DomainAdapter {
	RentEzApplication rentEzApplication = new RentEzApplication();
	
	@Override
	public Object getSystemUnderTest() {
		return rentEzApplication;
	}
	public void timeIsNow(@SuppressWarnings("unused") String time) {
		//
	}
	public ClientTransaction beginTransactionForClientStaff(Client client, @SuppressWarnings("unused") StaffMember staff) {
		return new ClientTransaction(client);
	}
	public Client findClient(String key) { 
		return rentEzApplication.findClient(key);
	}
	public StaffMember findStaffMember(String key) {
		return rentEzApplication.findStaffMember(key);
	}
	public RentalItemType findRentalItemType(String key) {
		return rentEzApplication.findRentalItemType(key);
	}
	public Rates findRates(@SuppressWarnings("unused") String key) { 
		return new Rates();
	}
	public Duration findDuration(@SuppressWarnings("unused") String key) {
		return new Duration();
	}
}
