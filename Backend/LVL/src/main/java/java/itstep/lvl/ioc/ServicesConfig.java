package java.itstep.LVL.ioc;

import com.google.inject.AbstractModule;
import java.itstep.LVL.services.config.ConfigService;
import java.itstep.LVL.services.config.JsonConfigService;
import java.itstep.LVL.services.hash.HashService;
import java.itstep.LVL.services.hash.Md5HashService;
import java.itstep.LVL.services.kdf.KdfService;
import java.itstep.LVL.services.kdf.PbKdf1Service;
import java.itstep.LVL.services.timestamp.TimestampService;
import java.itstep.LVL.services.timestamp.UnixSecondsTimestampService;

public class ServicesConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(KdfService.class).to(PbKdf1Service.class);
        bind(HashService.class).to(Md5HashService.class);
        bind(TimestampService.class).to(UnixSecondsTimestampService.class);
        bind(ConfigService.class).to(JsonConfigService.class)
                        .asEagerSingleton();
    }
}
