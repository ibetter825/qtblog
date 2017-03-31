/**
 * @license Copyright (c) 2003-2015, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config

	config.language = 'zh-cn';
	/*config.uiColor = '#4A8BC2';*/
	/*config.skin = 'minimalist';*/
	config.height = 300;
	config.toolbarCanCollapse = true;
	config.image_previewText = ' ';
	// 图片上传配置  
	config.filebrowserImageUploadUrl = '/upload/editor?upload_type=image';
	// flash上传配置
	config.filebrowserFlashUploadUrl = '/upload/editor?upload_type=flash';
	    
	// 图片浏览配置
	/*config.filebrowserUploadUrl = '/upload?upload_type=file&upload_from=editor'; 
	config.filebrowserImageBrowseUrl = 'browerServer.do?upload_type=image&upload_from=editor';*/
	
	// Remove some buttons provided by the standard plugins, which are
	// not needed in the Standard(s) toolbar.
	config.removeButtons = 'Underline,Subscript,Superscript';

	// Set the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Simplify the dialog windows.
	config.removeDialogTabs = 'image:advanced;link:advanced';
};
