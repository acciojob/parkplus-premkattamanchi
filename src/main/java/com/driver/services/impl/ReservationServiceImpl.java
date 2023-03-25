package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user=userRepository3.findById(userId).get();
        ParkingLot parkingLot=parkingLotRepository3.findById(parkingLotId).get();
        if(user==null || parkingLot==null)
            throw new Exception("Cannot make reservation");
        //finding Spot
        int minPricePerHour=Integer.MAX_VALUE;
        Spot requiredSpot=null;
        List<Spot> spotList=parkingLot.getSpotList();
        for(Spot spot:spotList){
            if(spot.isOccupied()==false){
                if(spot.getSpotType().equals(SpotType.TWO_WHEELER) && numberOfWheels<=2 && minPricePerHour>spot.getPricePerHour()){
                    minPricePerHour=spot.getPricePerHour();
                    requiredSpot=spot;
                }
                else if(spot.getSpotType().equals(SpotType.FOUR_WHEELER) && numberOfWheels<=4 && minPricePerHour>spot.getPricePerHour()){
                    minPricePerHour=spot.getPricePerHour();
                    requiredSpot=spot;
                }
                else if(spot.getSpotType().equals(SpotType.OTHERS) && numberOfWheels>4 && minPricePerHour>spot.getPricePerHour()){
                    minPricePerHour=spot.getPricePerHour();
                    requiredSpot=spot;
                }
            }
        }
        if(requiredSpot==null)
            throw new Exception("Cannot make reservation");
        Reservation reservation=new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(requiredSpot);
        reservation.setUser(user);
        reservationRepository3.save(reservation);

        requiredSpot.getReservationList().add(reservation);
        spotRepository3.save(requiredSpot);

        user.getReservationList().add(reservation);
        userRepository3.save(user);
        return reservation;
    }
}
