package de.trustable.ca3s.acmeproxy.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Generated;
import java.util.Optional;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-03-27T13:33:05.045561200+02:00[Europe/Berlin]")
@Controller
public class AcmeApiController implements AcmeApi {

    private final AcmeApiDelegate delegate;

    public AcmeApiController(@Autowired(required = false) AcmeApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new AcmeApiDelegate() {});
    }

    @Override
    public AcmeApiDelegate getDelegate() {
        return delegate;
    }

}
