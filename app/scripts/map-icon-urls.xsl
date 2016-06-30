<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">
                
	<xsl:output method="xml" 
				encoding="UTF-8"
				indent="yes" />
	
	<xsl:template match="/">
		<xsl:apply-templates select="*[name()='html']" />
	</xsl:template>
	
	<xsl:template match="*[name()='img']">

		<xsl:variable name="removeable">
			<xsl:text>../resources/icons/</xsl:text>
		</xsl:variable>

		<xsl:element name="img">
			<xsl:attribute name="src">
				
				<xsl:choose>
					<xsl:when test="contains(@src, $removeable)">
						<xsl:value-of select="substring-after(@src, $removeable)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@src"/>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:attribute>
			
			<xsl:apply-templates select="@*[name()!='src']" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:element name="{local-name()}">
			<!-- go process attributes and children -->
			<xsl:apply-templates select="@*|node()"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="@*">
		<xsl:attribute name="{local-name()}">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>