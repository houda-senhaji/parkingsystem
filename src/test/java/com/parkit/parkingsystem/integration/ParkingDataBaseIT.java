package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  //  private static ParkingSpotDAO parkingSpotDAO;
  //  private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private Ticket ticket;
  //  private static  InteractiveShell interactiveShell;
    
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
/**
    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }
*/
   
    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
       
    }
    
    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar()  throws Exception {
    	 when(inputReaderUtil.readSelection()).thenReturn(1);
         when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(2);
         when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	
    	ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR,false);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
     
        assertEquals(parkingService.getNextParkingNumberIfAvailable(),parkingSpot);
    }
 
    @Test
    public void testParkingABike()  throws Exception {
    	 when(inputReaderUtil.readSelection()).thenReturn(2);
         when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(2);
         when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	
    	ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.BIKE,false);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
     
        assertEquals(parkingService.getNextParkingNumberIfAvailable(),parkingSpot);
    }  
  
    
    
    @Test
    public void testParkingLotExit() throws Exception{
    	when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
    	ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR,false);
    	Date inTime = new Date();
         inTime.setTime( System.currentTimeMillis() - (  90 * 60 * 1000) );//1,5 hour parking time 
         ticket.setId(22);
         ticket.setInTime(inTime);
         ticket.setParkingSpot(parkingSpot);
         ticket.setIsLoyal(false);
    	
    	
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
       
        assertEquals( (1.5 * Fare.CAR_RATE_PER_HOUR ) , (int)(ticket.getPrice()*100)/100.0);
        System.out.println("Out Time is:" + ticket.getOutTime());
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
    
    @Test
    public void testCheckISLoyalTrue() throws Exception{
    	when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
    	when(ticketDAO.getNumberTicket("ABCDEF")).thenReturn(3);
    	
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
       
        assertEquals( ticket.getIsLoyal() , true);
        //TODO: check that the fare generated and out time are populated correctly in the database
    } 
  
   
    
}
