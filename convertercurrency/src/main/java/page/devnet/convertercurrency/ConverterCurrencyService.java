package page.devnet.convertercurrency;
/**
 * @author konstantin
 * @since 01.08.24
 */
public interface ConverterCurrencyService {

    String convert(String from) throws ConverterCurrencyException;


}
