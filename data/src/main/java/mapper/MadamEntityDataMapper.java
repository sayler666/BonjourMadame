package mapper;

import com.sayler.bonjourmadame.network.model.MadameDto;
import entity.Madam;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Mapper class used to transform {@link com.sayler.bonjourmadame.network.model.MadameDto} (in the net layer) to {@link entity.Madam} in the data layer.
 *
 * Created by lchromy on 26.05.15.
 */

@Singleton
public class MadamEntityDataMapper {

  @Inject
  public MadamEntityDataMapper() {
  }

  public Madam transform(MadameDto madameDto) {
    Madam madam = new Madam();
    if (madameDto != null) {
      madam.setName(madameDto.name);
      madam.setUrl(madameDto.url);
      madam.setType(madameDto.type);
    }

    return madam;
  }
}
