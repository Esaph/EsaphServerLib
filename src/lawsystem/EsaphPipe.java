/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package lawsystem;


import request.EsaphRequestHandler;

public interface EsaphPipe
{
	public String getPipeCommand();
	public void transfusion(EsaphRequestHandler.EsaphServerSession esaphServerSession);
}
