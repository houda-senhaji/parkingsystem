package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    
   
  
    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
       
    }

   
    
    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
    

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithLessThanHalfHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  25 * 60 * 1000) );//25 minutes parking time should give 0 parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithLessThanHalfHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  25 * 60 * 1000) );//25 minutes parking time should give 0 parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }

    @Test
    public void calculateFareCarForLoyalCustomerParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  90 * 60 * 1000) );//1,5 hour parking time 
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setIsLoyal(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (1.5 * Fare.CAR_RATE_PER_HOUR * 0.95) , ticket.getPrice());//5% reduction
    }
    
    @Test
    public void calculateFareBikeForLoyalCustomerParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  90 * 60 * 1000) );//1,5 hour parking time 
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setId(2);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setIsLoyal(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (1.5 * Fare.BIKE_RATE_PER_HOUR * 0.95) , ticket.getPrice());//5% reduction
    }
    
    @Test
    public void calculateTestId(){
        
        ticket.setId(2);
       
        assertEquals( ticket.getId() , 2);
    }

    @Test
    public void testParkingSpotMethodsId(){
    
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 parkingSpot.setId(2);
    	 assertEquals(parkingSpot.getId() , 2);
    }
    
    @Test
    public void testParkingSpotMethodsHashCode(){
    	
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 parkingSpot.setId(2);
    	 assertEquals(parkingSpot.hashCode() , 2);
    }
    
    
    @Test
    public void testParkingSpotMethodsType(){
    	
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 parkingSpot.setParkingType(ParkingType.BIKE);
    	 assertEquals(parkingSpot.getParkingType() , ParkingType.BIKE);
    } 
    
    @Test
    public void testParkingSpotMethodsAvail(){
    	
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 parkingSpot.setAvailable(true);
    	 assertEquals(parkingSpot.isAvailable() , true); 
    }	 
    
    @Test
    public void testParkingSpotMethodsEqualTrue(){
    	
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	
    	 ParkingSpot o = new ParkingSpot(1, ParkingType.CAR,false);
    	
    	 assertEquals(parkingSpot.equals(o) , true);  
    }
    
    @Test
    public void testParkingSpotMethodsEqualFalse(){
    	
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 ParkingSpot o = new ParkingSpot(2, ParkingType.BIKE,true);
    	
    	 assertEquals(parkingSpot.equals(o) , false);  
    }
    
   
    
}
