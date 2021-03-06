<%--

    Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License v. 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0.

    This Source Code may also be made available under the following Secondary
    Licenses when the conditions for such availability set forth in the
    Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
    version 2 with the GNU Classpath Exception, which is available at
    https://www.gnu.org/software/classpath/license.html.

    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0

--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%--
    Document   : uninstall
    Created on : 9 Nov, 2009, 12:26:19 PM
    Author     : mohit
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bundle Uninstaller</title>
    </head>
    <body>
        <form action="web/bundleuninstaller" method="post">
            BundleId : &nbsp;&nbsp;<input type="text" name="bundleId" value="" /> <br>
            <input type="submit" value="Uninstall Bundle" />
        </form>
        <br>
    </body>
</html>
