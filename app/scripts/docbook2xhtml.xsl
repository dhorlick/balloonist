<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:doc="http://docbook.org/ns/docbook"
				xmlns:xl="http://www.w3.org/1999/xlink">

	<xsl:output method="xml" 
	              encoding="UTF-8"
	              indent="no" />
	              
	<xsl:strip-space elements="*" />
	<xsl:preserve-space elements="address street email city state country postcode pob phone fax" />
	
	<xsl:param name="generate-toc-anchors">yes</xsl:param>
	<xsl:param name="disguise-email-addresses">no</xsl:param>
	
	<xsl:template match="/">
		<xsl:apply-templates select="doc:book | doc:article" />
	</xsl:template>
	
	<xsl:template match="doc:book | doc:article">
		<xsl:element name="html">
			<xsl:element name="head">
				<xsl:if test="doc:title">
					<xsl:element name="title">
						<xsl:value-of select="doc:title" />
					</xsl:element>
				</xsl:if>
				
				<xsl:element name="meta">
					<xsl:attribute name="http-equiv">
						<xsl:text>Content-Type</xsl:text>
					</xsl:attribute>
					
					<xsl:attribute name="content">
						<xsl:text>text/html; charset=utf-8</xsl:text>
					</xsl:attribute>
				</xsl:element>
				
				<xsl:element name="style">
					<xsl:attribute name="type">
						<xsl:text>text/css</xsl:text>
					</xsl:attribute>
					<xsl:text>h6 { font-style: italic;
							padding: 0;
							font-size: 12px;  }
					</xsl:text>
					<!-- Since the default font size for h6 in most browsers is intolerable -->
				</xsl:element>
				<!-- TODO insert modification date of source document, if possible -->
				
			</xsl:element>
			<xsl:element name="body">
				<xsl:apply-templates />
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:info">
		<xsl:text>Copyleft © </xsl:text>
		<xsl:value-of select="doc:copyright/doc:year" />
		<xsl:text> </xsl:text>
		<xsl:value-of select="doc:copyright/doc:holder" />
	</xsl:template>
	
	<xsl:template match="doc:para">
		<xsl:choose>
			<xsl:when test="name(..)='question'">	<!-- since we're in an xhtml list item -->
				<xsl:apply-templates select="@*|node()" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="p">
					<xsl:apply-templates select="@*|node()" />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="doc:title">
		<xsl:param name="depth">1</xsl:param>
		<xsl:param name="chapter-number" />
		<xsl:choose>
			<xsl:when test="name(..)='book' or name(..)='article'">
				<xsl:element name="h1">
					<xsl:apply-templates />
				</xsl:element>
			</xsl:when>
			<xsl:when test="name(..)='figure'">
				<xsl:element name="p">
					<xsl:element name="b">
						<xsl:text>Figure </xsl:text>
						<xsl:choose>
							<xsl:when test="$chapter-number">
								<xsl:value-of select="$chapter-number"/>
								<xsl:text>-</xsl:text>
								<xsl:number level="any" count="doc:figure" from="//doc:chapter" />
								<xsl:text>. </xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>: </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:apply-templates />
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:when test="name(..)='table'">
				<xsl:element name="p">
					<xsl:element name="b">
						<xsl:text>Table </xsl:text>
						<xsl:choose>
							<xsl:when test="$chapter-number">
								<xsl:value-of select="$chapter-number"/>
								<xsl:text>-</xsl:text>
								<xsl:number level="any" count="doc:table" from="//doc:chapter" />
								<xsl:text>. </xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>: </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:apply-templates />
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$depth=1">
						<xsl:element name="h2">
							<xsl:if test="name(..)='chapter'">
								<xsl:text>Chapter </xsl:text>
								<xsl:value-of select="$chapter-number" />
								<xsl:text>. </xsl:text>
							</xsl:if>
							<xsl:apply-templates />
						</xsl:element>
					</xsl:when>
					<xsl:when test="$depth=2">
						<xsl:element name="h3">
							<xsl:apply-templates />
						</xsl:element>
					</xsl:when>
					<xsl:when test="$depth=3">
						<xsl:element name="h4">
							<xsl:apply-templates />
						</xsl:element>
					</xsl:when>
					<xsl:when test="$depth=4">
						<xsl:element name="h5">
							<xsl:apply-templates />
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="h6">
							<xsl:apply-templates />
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="doc:emphasis">
		<xsl:element name="em">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>
	
	<xsl:template match="doc:code | doc:filename">
		<xsl:element name="code">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:link">
		<xsl:element name="a">
			<xsl:choose>
				<xsl:when test="@xl:href">
					<xsl:attribute name="href">
						<xsl:value-of select="@xl:href" />
					</xsl:attribute>
					<xsl:apply-templates />
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="href">
						<xsl:text>#</xsl:text>
						<xsl:value-of select="@linkend" />
					</xsl:attribute>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:imagedata">
		<xsl:element name="img">
			<xsl:attribute name="src">
				<xsl:value-of select="@fileref" />
			</xsl:attribute>
			<xsl:if test="@valign='bottom' or @valign='middle' or @valign='top'">
				<xsl:attribute name="align">
					<xsl:value-of select="@valign" />
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:itemizedlist">
		<xsl:element name="ul">
			<xsl:apply-templates select="doc:listitem" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:listitem">
		<xsl:element name="li">
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:address">
		<xsl:element name="pre">
			<xsl:apply-templates select="doc:street|doc:email|doc:city|doc:state|doc:country|doc:postcode|doc:pob|doc:phone|doc:fax|text()" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:street|doc:email|doc:city|doc:state|doc:country|doc:postcode|doc:pob|doc:phone|doc:fax"><xsl:apply-templates /></xsl:template>
	
	<xsl:template match="doc:table">
		<xsl:param name="chapter-number" />
		<xsl:apply-templates select="doc:title">
			<xsl:with-param name="chapter-number">
				<xsl:value-of select="$chapter-number" />
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:element name="table">
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:apply-templates select="doc:tgroup" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:tgroup">
		<xsl:apply-templates select="doc:tbody" />
	</xsl:template>
	
	<xsl:template match="doc:tbody">
		<xsl:apply-templates select="doc:row" />
	</xsl:template>

	<xsl:template match="doc:row">
		<xsl:element name="tr">
			<xsl:apply-templates select="doc:entry" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:entry">
		<xsl:element name="td">
			<xsl:apply-templates />
		</xsl:element> <!-- TODO restrict this to just certain elements -->
	</xsl:template>
	
	<xsl:template match="doc:section">
		<xsl:param name="depth">1</xsl:param>
		<xsl:param name="parent-code" />
		<xsl:param name="chapter-number" />
		
		<xsl:variable name="code">
			<xsl:value-of select="$parent-code" />
			<xsl:text>-section-</xsl:text>
			<xsl:value-of select="count(preceding-sibling::doc:section) + 1" />
					<!-- TODO ^^ come up with something faster than this -->
		</xsl:variable>
		
		<xsl:if test="$generate-toc-anchors='yes'">
			<xsl:element name="a">
				<xsl:attribute name="name">
					<xsl:value-of select="$code" />
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
		<xsl:apply-templates select="@*|node()">
			<xsl:with-param name="depth">
				<xsl:value-of select="$depth + 1" />
			</xsl:with-param>
			<xsl:with-param name="parent-code">
				<xsl:value-of select="$code" />
			</xsl:with-param>
			<xsl:with-param name="chapter-number">
				<xsl:value-of select="$chapter-number" />
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="@id|@xml:id">
		<xsl:element name="a" >
			<xsl:attribute name="name">
				<xsl:value-of select="." />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:chapter">
		<xsl:variable name="chapter-number">
			<xsl:value-of select="count(preceding-sibling::doc:chapter) + 1" />
					<!-- TODO ^^ come up with something faster than this -->
		</xsl:variable>
		<xsl:variable name="code">
			<xsl:text>chapter-</xsl:text>
			<xsl:value-of select="$chapter-number" />
		</xsl:variable>
		<xsl:if test="$generate-toc-anchors='yes'">
			<xsl:element name="a">
				<xsl:attribute name="name">
					<xsl:value-of select="$code" />
				</xsl:attribute>
			</xsl:element>
		</xsl:if>
		<xsl:apply-templates select="@*|node()">
			<xsl:with-param name="parent-code">
				<xsl:value-of select="$code" />
			</xsl:with-param>
			<xsl:with-param name="chapter-number">
				<xsl:value-of select="$chapter-number" />
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="doc:chapterinfo">
		<!-- TO DO -->
	</xsl:template>
	
	<xsl:template match="doc:indexterm">
		<!-- TO DO -->
	</xsl:template>
	
	<xsl:template match="doc:qandaset">
		<xsl:apply-templates mode="faq-toc" select="." />
		<xsl:element name="h3">
			<xsl:text>Question &amp; Answers</xsl:text>
		</xsl:element>
		<xsl:apply-templates select="doc:qandaentry" />
	</xsl:template>
	
	<xsl:template match="doc:qandaentry">
		<xsl:element name="a">
			<xsl:attribute name="name">
				<xsl:text>faq-q</xsl:text>
				<xsl:value-of select="position()" />
			</xsl:attribute>
		</xsl:element>
		<xsl:element name="p">
			<xsl:value-of select="position()" />
			<xsl:text>. </xsl:text>
			<xsl:element name="span">
				<xsl:attribute name="style">
					<xsl:text>font-weight:bold</xsl:text>
				</xsl:attribute>
				<xsl:apply-templates select="doc:question" />
			</xsl:element>
			<xsl:apply-templates select="doc:answer" />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="doc:question">
		<!-- TODO remove outer paragraph element if present -->
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="doc:answer">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="doc:*">
		<xsl:param name="chapter-number" />
		<xsl:apply-templates>
			<xsl:with-param name="chapter-number">
				<xsl:value-of select="$chapter-number" />
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="doc:qandaset" mode="faq-toc">
		<xsl:element name="h3">
			<xsl:text>Table of Contents</xsl:text>
		</xsl:element>
		<xsl:for-each select="doc:qandaentry">
			<xsl:element name="p">
				<xsl:value-of select="position()" />
				<xsl:text>. </xsl:text>
				<xsl:element name="a">
					<xsl:attribute name="href">
						<xsl:text>#faq-q</xsl:text>
						<xsl:value-of select="position()" />
					</xsl:attribute>
					<xsl:value-of select="doc:question" />
				</xsl:element>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="doc:email">
		<xsl:element name="span">
			<xsl:attribute name="style">font-style: oblique</xsl:attribute>
			<xsl:choose>
				<xsl:when test="$disguise-email-addresses='yes'">
					<xsl:variable name="halfway-there">
						<xsl:call-template name="replace-string">
							<xsl:with-param name="text" select="." />
							<xsl:with-param name="from" select="'@'"/>
							<xsl:with-param name="to" select="'-at-'"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:call-template name="replace-string">
						<xsl:with-param name="text" select="$halfway-there" />
						<xsl:with-param name="from" select="'.'"/>
						<xsl:with-param name="to" select="'-period-'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:text>mailto:</xsl:text>
							<xsl:value-of select="." />
						</xsl:attribute>
						<xsl:value-of select="." />
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="replace-string"> <!-- thanks, Paul Prescod -->
		<xsl:param name="text"/>
		<xsl:param name="from"/>
		<xsl:param name="to"/>
		
		<xsl:choose>
			<xsl:when test="contains($text, $from)">			
				<xsl:variable name="before" select="substring-before($text, $from)"/>
				<xsl:variable name="after" select="substring-after($text, $from)"/>
				<xsl:variable name="prefix" select="concat($before, $to)"/>			
				<xsl:value-of select="$before"/>
				<xsl:value-of select="$to"/>
				<xsl:call-template name="replace-string">
					<xsl:with-param name="text" select="$after"/>
					<xsl:with-param name="from" select="$from"/>
					<xsl:with-param name="to" select="$to"/>
				</xsl:call-template>
			</xsl:when> 
			<xsl:otherwise>
				<xsl:value-of select="$text"/>  
			</xsl:otherwise>
		</xsl:choose>            
	</xsl:template>
	
</xsl:stylesheet>
