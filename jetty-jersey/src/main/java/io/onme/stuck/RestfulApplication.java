package io.onme.stuck;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;

import io.onme.stuck.restful.SpotResource;

public class RestfulApplication extends ResourceConfig
{
	public RestfulApplication()
	{
		register( MoxyJsonFeature.class );
		register( MoxyXmlFeature.class );
		register( MultiPartFeature.class );
		packages( SpotResource.class.getPackage().getName() );
	}
}
