package mapper;

import com.sayler.bonjourmadame.network.model.MadameDto;
import entity.Madame;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Mapper class used to transform {@link com.sayler.bonjourmadame.network.model.MadameDto} (in the net layer) to {@link Madame} in the data layer.
 *
 * Created by lchromy on 26.05.15.
 */

@Singleton
public class MadamEntityDataMapper {

  @Inject
  public MadamEntityDataMapper() {
  }

  public Madame transform(MadameDto madameDto) {
    Madame madame = new Madame();
    if (madameDto != null) {
      madame.setName(madameDto.name);
      madame.setUrl(madameDto.url);
      madame.setType(madameDto.type);
    }

    return madame;
  }
}
