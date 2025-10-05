/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.services.timestamp;

/**
 *
 * @author pronc
 */
import com.google.inject.Singleton;
import java.time.Instant;

@Singleton
public class UnixSecondsTimestampService implements TimestampService {

    @Override
    public long nowSeconds() {
        return Instant.now().getEpochSecond();
    }
}
