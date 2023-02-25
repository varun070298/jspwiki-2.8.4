package com.ecyrd.jspwiki.plugin;

import com.ecyrd.jspwiki.*;

import java.util.*;

/**
 *  Implements a simple plugin that just returns a piece of Javascript
 *  <P>
 *  Parameters: text - text to return.
 *
 *  @author Janne Jalkanen
 */
public class JavaScriptPlugin
    implements WikiPlugin, InitializablePlugin
{
    protected static boolean c_inited = false;
    
    public String execute( WikiContext context, Map params )
        throws PluginException
    {
        return "<script language=\"JavaScript\"><!--\nfoo='';\n--></script>\n";
    }

    public void initialize(WikiEngine engine) throws PluginException
    {
        c_inited = true;
    }

}
