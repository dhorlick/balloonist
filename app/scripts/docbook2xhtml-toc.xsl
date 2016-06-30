<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:doc="http://docbook.org/ns/docbook">
                
	<xsl:output method="xml" 
			encoding="UTF-8"
			indent="yes" />
	
	<xsl:param name="target-level">2</xsl:param>
	
	<xsl:template match="/">
		<xsl:apply-templates select="doc:book" />
		<!-- TODO support the article doc root -->
	</xsl:template>
	
	<xsl:template match="doc:book">
		<xsl:element name="html">
			<xsl:element name="body">
				<xsl:element name="p">
					<xsl:element name="b">
						<xsl:text>Table of Contents</xsl:text>
					</xsl:element>
				</xsl:element>
				<xsl:element name="ol">
					<xsl:apply-templates select="doc:chapter|doc:section" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:section">
		<xsl:param name="depth">1</xsl:param>
		<xsl:param name="parent-code" />
		
		<xsl:variable name="code">
			<xsl:value-of select="$parent-code" />
			<xsl:text>-section-</xsl:text>
			<xsl:value-of select="position()" />
		</xsl:variable>
		
		<xsl:element name="li">
			<xsl:apply-templates select="doc:title">
				<xsl:with-param name="code">
					<xsl:value-of select="$code" />
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:if test="count(doc:section)>1 and $depth &lt; $target-level">
				<xsl:element name="ol">
					<xsl:apply-templates select="doc:section">
						<xsl:with-param name="depth">
							<xsl:value-of select="$depth + 1" />
						</xsl:with-param>
						<xsl:with-param name="parent-code">
							<xsl:value-of select="$code" />
						</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:title">
		<xsl:param name="code" />
			<xsl:element name="a">
				<xsl:if test="name(..) != 'book'">
					<xsl:attribute name="href">
						<xsl:text>content.html#</xsl:text>
						<xsl:value-of select="$code" />
					</xsl:attribute>
					<xsl:attribute name="target">
						<xsl:text>content-frame</xsl:text>
					</xsl:attribute>
				</xsl:if>	
				<xsl:value-of select="." />
			</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:chapter">
		<xsl:param name="depth">0</xsl:param>
		<xsl:variable name="code">
			<xsl:text>chapter-</xsl:text>
			<xsl:value-of select="position()" />
		</xsl:variable>
		<xsl:element name="li">
			<xsl:apply-templates select="doc:title">
				<xsl:with-param name="code">
					<xsl:value-of select="$code" />
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:if test="count(doc:section)>1 and $depth &lt; $target-level">
				<xsl:element name="ol">
					<xsl:apply-templates select="doc:section">
						<xsl:with-param name="depth">
							<xsl:value-of select="$depth + 1" />
						</xsl:with-param>
						<xsl:with-param name="parent-code">
							<xsl:value-of select="$code" />
						</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>