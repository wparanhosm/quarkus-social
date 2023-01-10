package io.github.wparanhosm.application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        tags = {
                @Tag(name="User Resource", description=" Endpoints definidos para criação/exclusão de usuários"),
                @Tag(name="Follower Resource", description="Endpoints definidos para a seguir, listar ou deixar de seguir usuários"),
                @Tag(name="Post Resource", description="Endpoints definidos para criar posts e listar posts se você segue o usuário selecionado")

        },
        info = @Info(
                title="API de Exemplo Quarkus",
                version = "1.0.1",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class QuarkusSocialApplication extends Application {
}
