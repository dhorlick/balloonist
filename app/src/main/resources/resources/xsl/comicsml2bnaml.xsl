<?xml version="1.0" encoding="UTF-8" ?>
<!--
Copyleft by Dave Horlick
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" />
	
	<xsl:param name="strip-index">1</xsl:param>
	<xsl:param name="font-family">sans-serif</xsl:param>
	<xsl:param name="font-size">12</xsl:param>
	<xsl:param name="margin">10.0</xsl:param>
	
	<xsl:strip-space elements="text" />
	
    <xsl:template match="/comic">
        <xsl:element name="balloonist-document">
			<xsl:attribute name="version">1.0</xsl:attribute>
			<xsl:apply-templates select="strip[number($strip-index)]" />
        </xsl:element>
    </xsl:template>

	<xsl:template match="strip">
			
		<xsl:element name="layout">
			<xsl:attribute name="apertures">
				<xsl:value-of select="count(panels/panel)" />
			</xsl:attribute>
			
			<xsl:attribute name="aspect-ratio">1.374633</xsl:attribute>

			<xsl:attribute name="hgap">10.0</xsl:attribute>
			<xsl:attribute name="hmargin">60.0</xsl:attribute>

			<xsl:attribute name="vgap">10.0</xsl:attribute>
			<xsl:attribute name="vmargin">50.0</xsl:attribute>
			
			<xsl:attribute name="prevent-orphans">false</xsl:attribute>
				<!-- TODO ^^ remove? -->
			
			<!-- begin placeholders -->
			
				<xsl:attribute name="height">541</xsl:attribute>
				<xsl:attribute name="width">727</xsl:attribute>
			
				<xsl:attribute name="x">0</xsl:attribute>
				<xsl:attribute name="y">0</xsl:attribute>
			
			<!-- end placeholders -->
			
		</xsl:element>
		
		<xsl:element name="page">
			<xsl:attribute name="number">1</xsl:attribute>
			
			<xsl:element name="sill">
				<xsl:attribute name="inner-margin"><xsl:value-of select="$margin"/></xsl:attribute>
				<xsl:attribute name="outer-margin"><xsl:value-of select="-1 * $margin"/></xsl:attribute>
				
				<xsl:attribute name="name">page</xsl:attribute>
					<!-- use comic/title instead? -->
				
				<!-- begin placeholders -->
				
					<xsl:attribute name="x">60.0</xsl:attribute>
					<xsl:attribute name="y">50.0</xsl:attribute>
				
				<!-- end placeholders -->
				
				<xsl:apply-templates select="panels" />
				
			</xsl:element>
			
		</xsl:element>
		
	</xsl:template>
	
	<xsl:template match="panels">
		<xsl:apply-templates select="panel" />
	</xsl:template>
	
	<xsl:template match="panel">

		<xsl:element name="sill">
			<xsl:attribute name="inner-margin"><xsl:value-of select="$margin"/></xsl:attribute>
			<xsl:attribute name="outer-margin"><xsl:value-of select="-1 * $margin"/></xsl:attribute>

			<xsl:attribute name="name">
				<xsl:text>panel </xsl:text>
				<xsl:value-of select="position()" />
			</xsl:attribute>
			
			<!-- begin placeholders -->

				<xsl:attribute name="x">0.0</xsl:attribute>
				<xsl:attribute name="y">0.0</xsl:attribute>

			<!-- end placeholders -->
			
			<xsl:element name="aperture">
				<xsl:attribute name="fill-color">#ffffffff</xsl:attribute>
				<xsl:attribute name="outline-color">#ff000000</xsl:attribute>
				<xsl:attribute name="border-width">1.0</xsl:attribute>
				
				<xsl:element name="shape">
					<xsl:element name="rect">
						
						<!-- begin placeholders -->
						
							<xsl:attribute name="x">0.0</xsl:attribute>
							<xsl:attribute name="y">0.0</xsl:attribute>
							<xsl:attribute name="width">359.0</xsl:attribute>
							<xsl:attribute name="height">261.1</xsl:attribute>
						
						<!-- end placeholders -->
						
					</xsl:element>
				</xsl:element>
				
			</xsl:element>

			<!-- TODO map url -->

			<xsl:apply-templates select="panel-desc" />
		
		</xsl:element>

	</xsl:template>
	
	<xsl:template match="panel-desc">
		<xsl:element name="crowd">
			<xsl:apply-templates select="speech|thought|narration" />
			
			<!-- ^^ The initial positions don't matter, because Balloonist will arrange them upon import -->
			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="speech">
		
		<xsl:element name="balloon">

			<xsl:attribute name="fill-color">#ffffffff</xsl:attribute>
			<xsl:attribute name="outline-color">#ff000000</xsl:attribute>
			<xsl:attribute name="inner-margin"><xsl:value-of select="$margin"/></xsl:attribute>
			<xsl:attribute name="outer-margin"><xsl:value-of select="-1 * $margin"/></xsl:attribute>

			<xsl:attribute name="vertical">false</xsl:attribute>
				<!-- TODO ^^ if language is Chinese or Japanese, set this to true -->

			<xsl:element name="edition">
				
				<xsl:element name="super-ellipse">

					<xsl:attribute name="hein-parameter">0.6</xsl:attribute>
					
					<xsl:attribute name="x">250.0</xsl:attribute>
					<xsl:attribute name="y">120.0</xsl:attribute>

					<xsl:attribute name="rx">80.0</xsl:attribute>
					<xsl:attribute name="ry">40.0</xsl:attribute>
					
					<xsl:element name="stem">
						<xsl:attribute name="bubble-period">14.0</xsl:attribute>
						
						<xsl:attribute name="focus-x">150.0</xsl:attribute>
						<xsl:attribute name="focus-y">160.0</xsl:attribute>
							<!-- TODO ^^ tailor these -->
							
						<xsl:attribute name="leading-edge-position-as-perimeter-fraction">0.3</xsl:attribute>
						<xsl:attribute name="root-width">12.0</xsl:attribute>
						<xsl:attribute name="type">icicle</xsl:attribute>
						
					</xsl:element>
				
				</xsl:element>
		
				<xsl:apply-templates select="text" />
				
			</xsl:element>

		</xsl:element>
		
	</xsl:template>

	<xsl:template match="thought">
		
		<xsl:element name="balloon">

			<xsl:attribute name="fill-color">#ffffffff</xsl:attribute>
			<xsl:attribute name="outline-color">#ff000000</xsl:attribute>
			<xsl:attribute name="inner-margin"><xsl:value-of select="$margin"/></xsl:attribute>
			<xsl:attribute name="outer-margin"><xsl:value-of select="-1 * $margin"/></xsl:attribute>

			<xsl:attribute name="vertical">false</xsl:attribute>
				<!-- TODO ^^ if language is Chinese or Japanese, set this to true -->

			<xsl:element name="edition">
				
				<xsl:element name="super-ellipse">

					<xsl:attribute name="hein-parameter">1.0</xsl:attribute>
					
					<xsl:attribute name="x">250.0</xsl:attribute>
					<xsl:attribute name="y">120.0</xsl:attribute>

					<xsl:attribute name="rx">80.0</xsl:attribute>
					<xsl:attribute name="ry">40.0</xsl:attribute>
					
					<xsl:element name="ruffles">
						
						<xsl:element name="ruffle-die">
							<xsl:attribute name="height">5.0</xsl:attribute>
							<xsl:attribute name="preferred-width">27.0</xsl:attribute>
						</xsl:element>
						
						<xsl:element name="ruffle-die">
							<xsl:attribute name="height">3.0</xsl:attribute>
							<xsl:attribute name="preferred-width">15.0</xsl:attribute>
						</xsl:element>
						
					</xsl:element>
					
					<xsl:element name="stem">
						<xsl:attribute name="bubble-period">14.0</xsl:attribute>
						
						<xsl:attribute name="focus-x">210.0</xsl:attribute>
						<xsl:attribute name="focus-y">165.0</xsl:attribute>
							
						<xsl:attribute name="leading-edge-position-as-perimeter-fraction">0.3</xsl:attribute>
						<xsl:attribute name="root-width">12.0</xsl:attribute>
						<xsl:attribute name="type">bubbled</xsl:attribute>
						
					</xsl:element>
				
				</xsl:element>
		
				<xsl:apply-templates select="text" />
				
			</xsl:element>

		</xsl:element>		
	</xsl:template>

	<xsl:template match="narration">
		<xsl:element name="balloon">

			<xsl:attribute name="fill-color">#ffffff00</xsl:attribute>
			<xsl:attribute name="outline-color">#ff000000</xsl:attribute>
			<xsl:attribute name="inner-margin"><xsl:value-of select="$margin"/></xsl:attribute>
			<xsl:attribute name="outer-margin"><xsl:value-of select="-1 * $margin"/></xsl:attribute>

			<xsl:attribute name="vertical">false</xsl:attribute>
				<!-- TODO ^^ if language is Chinese or Japanese, set this to true -->

			<xsl:element name="edition">
				
				<xsl:element name="parallelogram">

					<xsl:attribute name="x">250.0</xsl:attribute>
					<xsl:attribute name="y">120.0</xsl:attribute>

					<xsl:attribute name="width-in-points">175.0</xsl:attribute>
					<xsl:attribute name="height-in-points">75.0</xsl:attribute>
					
					<xsl:attribute name="inset">12.0</xsl:attribute>
					
				</xsl:element>
		
				<xsl:call-template name="process-text" />
				
			</xsl:element>

		</xsl:element>
	</xsl:template>
	
	<xsl:template name="process-text" match="text">
		
		<xsl:element name="text">
			<xsl:element name="tspan">
			
				<xsl:attribute name="style">
					<xsl:text>font-size:</xsl:text>
					<xsl:value-of select="$font-size" />
					<xsl:text>; font-family:</xsl:text>
					<xsl:value-of select="$font-family" />
				</xsl:attribute>
				
				<xsl:apply-templates select="text()|bang|interro|interrobang|silence|emphasis|strong|soft" />
			</xsl:element>
		</xsl:element>
				
	</xsl:template>
	
	<xsl:template match="text()">
		<xsl:value-of select="normalize-space(.)" />
	</xsl:template>
	
	<xsl:template match="bang">
		<xsl:text>!</xsl:text>
	</xsl:template>
	
	<xsl:template match="interro">
		<xsl:text>?</xsl:text>
	</xsl:template>

	<xsl:template match="interrobang">
		<xsl:text>?!</xsl:text>
	</xsl:template>

	<xsl:template match="silence">
		<xsl:text>...</xsl:text>
	</xsl:template>
	
	<xsl:template match="emphasis">
		<xsl:element name="tspan">
			<xsl:attribute name="style">font-weight:bold</xsl:attribute>
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

	<xsl:template match="strong">
		<xsl:element name="tspan">
			<xsl:attribute name="style">font-style:italics</xsl:attribute>
			<xsl:value-of select="." />
		</xsl:element>
	</xsl:template>

	<xsl:template match="soft">
		<xsl:value-of select="." />
			<!-- TODO think of a good way to illustrate this -->
	</xsl:template>
	
</xsl:stylesheet>
