package proj.itstep.lvl.ioc;

import com.google.inject.AbstractModule;
import proj.itstep.lvl.services.config.ConfigService;
import proj.itstep.lvl.services.config.JsonConfigService;
import proj.itstep.lvl.services.hash.HashService;
import proj.itstep.lvl.services.hash.Md5HashService;
import proj.itstep.lvl.services.kdf.KdfService;
import proj.itstep.lvl.services.kdf.PbKdf1Service;
import proj.itstep.lvl.services.timestamp.TimestampService;
import proj.itstep.lvl.services.timestamp.UnixSecondsTimestampService;

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
