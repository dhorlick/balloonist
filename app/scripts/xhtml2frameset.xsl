<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">
                
	<xsl:output method="xml" 
				encoding="UTF-8"
				indent="yes" />
	
	<xsl:param name="title">Untitled</xsl:param>
	
	<xsl:template match="/">

		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<title>
				<xsl:value-of select="$title" />
			</title>
		</head>
		
		<frameset cols="30%,70%">
			<frame src="toc.html" name="toc-frame" title="Title of Contents" />
			<frame src="content.html" name="content-frame" scrolling="yes">
				<xsl:attribute name="title">
					<xsl:value-of select="$title" />
				</xsl:attribute>
			</frame>
			<noframes>
				<xsl:copy-of select="//*[name()='body']" />
			</noframes>
		
		</frameset>
		
		</html>

	</xsl:template>
	
</xsl:stylesheet>