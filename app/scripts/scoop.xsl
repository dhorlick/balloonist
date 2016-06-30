<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
		xmlns:doc="http://docbook.org/ns/docbook">
	<xsl:output method="xml" indent="no" />
	
	<!-- The chapter id to scoop out -->
	<xsl:param name="scoopable">terms-and-conditions</xsl:param>
	
	<xsl:template match="/">
		<xsl:apply-templates select="doc:book" />
	</xsl:template>

	<xsl:template match="doc:book">
		<xsl:element name="doc:book">
			<xsl:apply-templates select="doc:chapter | doc:section[@xml:id=$scoopable or @id=$scoopable]" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="doc:chapter">
		<xsl:apply-templates select="doc:section[@xml:id=$scoopable or @id=$scoopable]" />
	</xsl:template>
	
	<xsl:template match="doc:section">
		<xsl:if test="doc:title">
			<xsl:element name="doc:title">
				<xsl:value-of select="doc:title" />
			</xsl:element>
		</xsl:if>
		<xsl:copy-of select="*[name()!='title']" />
	</xsl:template>

</xsl:stylesheet>
