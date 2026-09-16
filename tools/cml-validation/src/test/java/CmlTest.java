import org.contextmapper.dsl.ContextMappingDSLStandaloneSetup;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.validation.*;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.diagnostics.Severity;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
class CmlTest {
    @Test void modelHasNoSyntaxOrSemanticErrors() {
        var injector=new ContextMappingDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
        var set=injector.getInstance(XtextResourceSet.class);
        var path=Path.of("../../docs/modelo/maos-em-acao.cml").toAbsolutePath().normalize();
        var resource=set.getResource(URI.createFileURI(path.toString()),true);
        var issues=injector.getInstance(IResourceValidator.class).validate(resource,CheckMode.ALL,CancelIndicator.NullImpl);
        var errors=issues.stream().filter(i->i.getSeverity()==Severity.ERROR).toList();
        assertTrue(errors.isEmpty(),()->"Erros CML: "+errors);
    }
}
