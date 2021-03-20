package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;


@ExtendWith(MockitoExtension.class)
class TicketDAOTest {

	 private  static Ticket ticket;
	 private static FareCalculatorService fareCalculatorService;
	 private static TicketDAO ticketDAO;
	 
	 @Mock
	    private static DataBaseTestConfig dataBaseTestConfig;
	 @Mock
	    private static Connection con;
	 @Mock
	 private static PreparedStatement ps ;
	 @Mock
	 private static  ResultSet rs;
	 
	 @BeforeAll
	    private static void setUp() {
	        fareCalculatorService = new FareCalculatorService();
	        
	    }
	
	 @BeforeEach
	    private void setUpPerTest() throws Exception {
	        ticket = new Ticket();
	        ticketDAO = new TicketDAO();
	        ticketDAO.dataBaseConfig = dataBaseTestConfig;

	    	when(dataBaseTestConfig.getConnection()).thenReturn(con);
	    	
	        Date inTime = new Date();
	        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
	        Date outTime = new Date();
	        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
	        
	        ticket.setId(2);
	        ticket.setParkingSpot(parkingSpot);
	        ticket.setVehicleRegNumber("ABCD");
	        ticket.setInTime(inTime);
	        ticket.setOutTime(outTime);
	        ticket.setIsLoyal(false);
	        fareCalculatorService.calculateFare(ticket);
	        
	        
	        
	        
	        
	    }
	
	 
	@Test
	void testSaveTicket() throws Exception{
		when(con.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(ps);
		assertEquals(ticketDAO.saveTicket(ticket),false);
		
		
	}
	
	@Test
	void testGetTicket() throws Exception{
		when(con.prepareStatement(DBConstants.GET_TICKET)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);		
		when(rs.getInt(Mockito.anyInt())).thenReturn(1,ticket.getId());
		when(rs.getString(6)).thenReturn("CAR");		
		when(rs.getDouble(3)).thenReturn(ticket.getPrice());		
		
		 Timestamp tsin = new Timestamp((ticket.getInTime()).getTime());
		 Timestamp tsout = new Timestamp((ticket.getOutTime()).getTime());
		 when(rs.getTimestamp(Mockito.anyInt())).thenReturn(tsin,tsout);
				
		 Ticket ticketGen = ticketDAO.getTicket("ABCD");
		 
					
		 DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");	
		 		
		
		 assertEquals(ticketGen.getId(),ticket.getId());
		 assertEquals(ticketGen.getParkingSpot(),ticket.getParkingSpot());
		 assertEquals(ticketGen.getVehicleRegNumber(),ticket.getVehicleRegNumber());
		 assertEquals(ticketGen.getPrice(),ticket.getPrice());
		 assertEquals(format.format(ticket.getInTime()),format.format((ticketGen).getInTime()));
		 assertEquals(format.format(ticket.getOutTime()),format.format(ticketGen.getOutTime()));
		 
		 
	}
	
	@Test
	void testUpdateTicket() throws Exception{
		
		when(con.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(ps);
		assertEquals(ticketDAO.updateTicket(ticket), true);
	}
	
	@Test
	void testGetNumberTicket() throws Exception{
		
		
		when(con.prepareStatement(DBConstants.GET_NUMBER_TICKET)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
	    when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(4);
		assertEquals(ticketDAO.getNumberTicket("ABCD"), 4);
		
	}
	

}
